package ru.itmo.credithistory.reportservice.service;

import com.google.common.util.concurrent.Striped;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.commons.exception.NotFoundException;
import ru.itmo.credithistory.commons.kafka.KafkaTopics;
import ru.itmo.credithistory.commons.kafka.dto.ReportServiceScoringRequestDto;
import ru.itmo.credithistory.commons.kafka.dto.ReportServiceScoringResponseDto;
import ru.itmo.credithistory.dataservice.grpc.Credit;
import ru.itmo.credithistory.dataservice.grpc.DataServiceGrpc;
import ru.itmo.credithistory.dataservice.grpc.GetCustomerDataRequest;
import ru.itmo.credithistory.dataservice.grpc.GetCustomerDataResponse;
import ru.itmo.credithistory.reportservice.configuration.ReportConfigurationProperties;
import ru.itmo.credithistory.reportservice.dto.GetOrCreateReportRequestResultDto;
import ru.itmo.credithistory.reportservice.dto.ReportDataDto;
import ru.itmo.credithistory.reportservice.enm.ReportRequestStatus;
import ru.itmo.credithistory.reportservice.entity.ReportRequest;
import ru.itmo.credithistory.reportservice.mapper.ReportDataMapper;
import ru.itmo.credithistory.reportservice.repository.ReportRequestRepository;
import ru.itmo.credithistory.userservice.grpc.CreditOrganization;
import ru.itmo.credithistory.userservice.grpc.GetCreditOrganizationsByIdsRequest;
import ru.itmo.credithistory.userservice.grpc.UserServiceGrpc;

@Slf4j
@Service
@RequiredArgsConstructor
@NullMarked
public class ReportRequestService {

  private final ReportRequestRepository reportRequestRepository;
  private final GenerationService generationService;
  private final ReportDataMapper reportDataMapper;
  private final ReportConfigurationProperties reportConfigurationProperties;
  private final KafkaTemplate<String, ReportServiceScoringRequestDto> kafkaTemplate;
  private final UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;
  private final DataServiceGrpc.DataServiceBlockingStub dataServiceBlockingStub;
  private final Striped<Lock> locks = Striped.lazyWeakLock(64);

  @KafkaListener(topics = KafkaTopics.REPORT_SERVICE_SCORING_RESPONSE)
  public void receiveScoringResponseFromScoringService(
      ReportServiceScoringResponseDto reportServiceScoringResponseDto) {
    log.info(
        "Received scoring response from scoring service (reportRequestId: {}, result: {}, success: {})",
        reportServiceScoringResponseDto.getReportRequestId(),
        reportServiceScoringResponseDto.getResult(),
        reportServiceScoringResponseDto.getSuccess());

    ReportRequest reportRequest =
        getReportRequestById(reportServiceScoringResponseDto.getReportRequestId());
    if (reportRequest == null) {
      log.error(
          "Report request with id {} not found",
          reportServiceScoringResponseDto.getReportRequestId());
      return;
    }

    if (!reportServiceScoringResponseDto.getSuccess()) {
      setReportRequestFailed(reportRequest);
      return;
    }

    Lock lock = locks.get(reportRequest.getCustomerInn());
    try {
      GetCustomerDataResponse customerData =
          dataServiceBlockingStub.getCustomerData(
              GetCustomerDataRequest.newBuilder()
                  .setCustomerInn(reportRequest.getCustomerInn())
                  .build());
      List<String> creditOrganizationIds =
          customerData.getCreditsList().stream()
              .map(Credit::getCreditOrganizationId)
              .distinct()
              .toList();
      List<CreditOrganization> creditOrganizations =
          userServiceBlockingStub
              .getCreditOrganizationsByIds(
                  GetCreditOrganizationsByIdsRequest.newBuilder()
                      .addAllIds(creditOrganizationIds)
                      .build())
              .getCreditOrganizationsList();

      Map<String, String> creditOrganizationIdNameMap =
          creditOrganizations.stream()
              .collect(
                  Collectors.toMap(CreditOrganization::getId, CreditOrganization::getShortName));
      ReportDataDto reportDataDto =
          reportDataMapper.fromCustomerDataGrpcToReportDataDto(
              customerData,
              reportServiceScoringResponseDto.getResult(),
              creditOrganizationIdNameMap);

      log.info(
          "Started report generation for id: {}, customerInn: {}",
          reportRequest.getId(),
          reportRequest.getCustomerInn());
      byte[] report =
          generationService.generateCreditReport(reportDataDto).getBytes(StandardCharsets.UTF_8);
      log.info(
          "Finished report generation for id: {}, customerInn: {}",
          reportRequest.getId(),
          reportRequest.getCustomerInn());

      lock.lock();
      reportRequest.setStatus(ReportRequestStatus.GENERATION_FINISHED);
      reportRequest.setReport(report);
      reportRequestRepository.save(reportRequest);
      lock.unlock();
    } catch (Exception e) {
      log.error("Failed report generation", e);
      setReportRequestFailed(reportRequest);
    }
  }

  @PreAuthorize("#customerInn == #requesterCustomerInn")
  @Transactional
  public GetOrCreateReportRequestResultDto getOrCreateReportRequest(
      String customerInn, @SuppressWarnings("unused") @Nullable String requesterCustomerInn) {
    Lock lock = locks.get(customerInn);
    lock.lock();

    try {
      boolean isCreated = false;
      ReportRequest reportRequest = getActualReportRequest(customerInn);

      if (reportRequest == null) {
        isCreated = true;

        reportRequest = new ReportRequest();
        reportRequest.setCustomerInn(customerInn);
        reportRequestRepository.save(reportRequest);

        kafkaTemplate.send(
            KafkaTopics.REPORT_SERVICE_SCORING_REQUEST,
            ReportServiceScoringRequestDto.builder()
                .customerInn(customerInn)
                .reportRequestId(reportRequest.getId())
                .build());
      }

      return GetOrCreateReportRequestResultDto.builder()
          .reportRequest(reportRequest)
          .isCreated(isCreated)
          .build();
    } finally {
      lock.unlock();
    }
  }

  @PreAuthorize("#customerInn == #requesterCustomerInn")
  @Transactional(readOnly = true)
  public byte[] getReport(
      String customerInn, @SuppressWarnings("unused") @Nullable String requesterCustomerInn) {
    ReportRequest reportRequest = getActualReportRequest(customerInn);
    if (reportRequest == null || reportRequest.getReport() == null) {
      throw new NotFoundException();
    }
    return reportRequest.getReport();
  }

  @Transactional
  private @Nullable ReportRequest getActualReportRequest(String customerInn) {
    LocalDateTime minActualTime =
        LocalDateTime.now().minus(reportConfigurationProperties.getActualTime());
    return reportRequestRepository.findActualReportRequest(customerInn, minActualTime).orElse(null);
  }

  public @Nullable ReportRequest getReportRequestById(UUID id) {
    return reportRequestRepository.findById(id).orElse(null);
  }

  @Transactional
  private void setReportRequestFailed(ReportRequest reportRequest) {
    log.error("Report request with id {} failed", reportRequest.getId());
    Lock lock = locks.get(reportRequest.getCustomerInn());

    try {
      lock.lock();
      reportRequest.setStatus(ReportRequestStatus.GENERATION_FAILED);
      reportRequestRepository.save(reportRequest);
    } finally {
      lock.unlock();
    }
  }
}
