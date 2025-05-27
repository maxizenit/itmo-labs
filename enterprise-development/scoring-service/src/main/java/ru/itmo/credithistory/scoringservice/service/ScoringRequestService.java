package ru.itmo.credithistory.scoringservice.service;

import com.google.common.util.concurrent.Striped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.commons.kafka.KafkaTopics;
import ru.itmo.credithistory.commons.kafka.dto.ReportServiceScoringRequestDto;
import ru.itmo.credithistory.commons.kafka.dto.ReportServiceScoringResponseDto;
import ru.itmo.credithistory.scoringservice.configuration.ScoringConfigurationProperties;
import ru.itmo.credithistory.scoringservice.dto.GetOrCreateScoringRequestResultDto;
import ru.itmo.credithistory.scoringservice.enm.ScoringRequestStatus;
import ru.itmo.credithistory.scoringservice.entity.ScoringRequest;
import ru.itmo.credithistory.scoringservice.repository.ScoringRequestRepository;

@Slf4j
@Service
@NullMarked
public class ScoringRequestService {

  private final ScoringRequestRepository scoringRequestRepository;
  private final CalculationService calculationService;
  private final KafkaTemplate<String, ReportServiceScoringResponseDto> kafkaTemplate;
  private final ScoringConfigurationProperties scoringConfigurationProperties;
  private final ExecutorService executorService;
  private final Striped<Lock> locks = Striped.lazyWeakLock(64);

  public ScoringRequestService(
      ScoringRequestRepository scoringRequestRepository,
      CalculationService calculationService,
      KafkaTemplate<String, ReportServiceScoringResponseDto> kafkaTemplate,
      ScoringConfigurationProperties scoringConfigurationProperties) {
    this.scoringRequestRepository = scoringRequestRepository;
    this.calculationService = calculationService;
    this.kafkaTemplate = kafkaTemplate;
    this.scoringConfigurationProperties = scoringConfigurationProperties;
    executorService = Executors.newFixedThreadPool(scoringConfigurationProperties.getParallelism());
  }

  @PreAuthorize("#customerInn == #requesterCustomerInn")
  @Transactional
  public GetOrCreateScoringRequestResultDto getOrCreateScoringRequest(
      String customerInn,
      @SuppressWarnings("unused") @Nullable String requesterCustomerInn,
      @Nullable UUID reportRequestId) {
    Lock lock = locks.get(customerInn);
    lock.lock();

    try {
      boolean isCreated = false;
      ScoringRequest scoringRequest = getActualScoringRequest(customerInn, reportRequestId);

      if (scoringRequest == null) {
        isCreated = true;

        scoringRequest = new ScoringRequest();
        scoringRequest.setCustomerInn(customerInn);
        scoringRequest.setReportRequestId(reportRequestId);
        scoringRequestRepository.save(scoringRequest);
      }

      return GetOrCreateScoringRequestResultDto.builder()
          .scoringRequest(scoringRequest)
          .isCreated(isCreated)
          .build();
    } finally {
      lock.unlock();
    }
  }

  @KafkaListener(topics = KafkaTopics.REPORT_SERVICE_SCORING_REQUEST)
  public void receiveScoringRequestFromReportService(
      ReportServiceScoringRequestDto reportServiceScoringRequestDto) {
    log.info(
        "Received scoring request from report service (customerInn: {}, reportRequestId: {})",
        reportServiceScoringRequestDto.getCustomerInn(),
        reportServiceScoringRequestDto.getReportRequestId());

    GetOrCreateScoringRequestResultDto getOrCreateScoringRequestResultDto =
        getOrCreateScoringRequest(
            reportServiceScoringRequestDto.getCustomerInn(),
            null,
            reportServiceScoringRequestDto.getReportRequestId());

    if (!getOrCreateScoringRequestResultDto.isCreated()) {
      ScoringRequest scoringRequest = getOrCreateScoringRequestResultDto.getScoringRequest();
      Lock lock = locks.get(scoringRequest.getCustomerInn());
      lock.lock();
      scoringRequest.setReportRequestId(reportServiceScoringRequestDto.getReportRequestId());
      scoringRequestRepository.save(scoringRequest);
      lock.unlock();

      if (ScoringRequestStatus.CALCULATION_FINISHED.equals(scoringRequest.getStatus())) {
        sendScoringResultToReportService(scoringRequest);
      }
    }
  }

  @Scheduled(fixedDelayString = "10s")
  public void startCalculationForBatch() {
    log.info(
        "Start calculation for batch with size: {}, parallelism: {}",
        scoringConfigurationProperties.getBatchSize(),
        scoringConfigurationProperties.getParallelism());

    List<ScoringRequest> batch = getUnprocessedRequestsBatch();
    if (batch.isEmpty()) {
      log.info("No actual scoring requests");
      return;
    }

    batch.forEach(
        scoringRequest -> executorService.submit(() -> processScoringRequest(scoringRequest)));
  }

  @Transactional
  private @Nullable ScoringRequest getActualScoringRequest(
      String customerInn, @Nullable UUID reportRequestId) {
    LocalDateTime minActualTime =
        LocalDateTime.now().minus(scoringConfigurationProperties.getActualTime());
    ScoringRequest scoringRequest =
        scoringRequestRepository.findActualScoringRequest(customerInn, minActualTime).orElse(null);

    if (scoringRequest != null && reportRequestId != null) {
      scoringRequest.setReportRequestId(reportRequestId);
      scoringRequestRepository.save(scoringRequest);
    }
    return scoringRequest;
  }

  private List<ScoringRequest> getUnprocessedRequestsBatch() {
    return scoringRequestRepository.findAllByStatusOrderByCreatedAt(
        ScoringRequestStatus.READY,
        PageRequest.of(0, scoringConfigurationProperties.getBatchSize()));
  }

  @Transactional
  private void processScoringRequest(ScoringRequest scoringRequest) {
    log.info(
        "Started calculation scoring for request with id: {}, customerInn: {}",
        scoringRequest.getId(),
        scoringRequest.getCustomerInn());

    Lock lock = locks.get(scoringRequest.getCustomerInn());
    try {
      lock.lock();
      scoringRequest.setStatus(ScoringRequestStatus.CALCULATION_IN_PROGRESS);
      scoringRequestRepository.save(scoringRequest);
      lock.unlock();

      int rating = calculationService.calculateCreditRating(scoringRequest.getCustomerInn());

      lock.lock();
      scoringRequest.setStatus(ScoringRequestStatus.CALCULATION_FINISHED);
      scoringRequest.setResult(rating);
      scoringRequestRepository.save(scoringRequest);
      lock.unlock();

      log.info(
          "Calculation for scoring request with id: {}, customerInn: {} completed",
          scoringRequest.getId(),
          scoringRequest.getCustomerInn());
    } catch (Exception e) {
      log.info(
          "Calculation for scoring request with id: {}, customerInn: {} failed",
          scoringRequest.getId(),
          scoringRequest.getCustomerInn());

      lock.lock();
      scoringRequest.setStatus(ScoringRequestStatus.CALCULATION_FAILED);
      lock.unlock();
    }

    if (scoringRequest.getReportRequestId() != null) {
      sendScoringResultToReportService(scoringRequest);
    }
  }

  private void sendScoringResultToReportService(ScoringRequest scoringRequest) {
    kafkaTemplate.send(
        KafkaTopics.REPORT_SERVICE_SCORING_RESPONSE,
        ReportServiceScoringResponseDto.builder()
            .reportRequestId(scoringRequest.getReportRequestId())
            .result(scoringRequest.getResult())
            .success(ScoringRequestStatus.CALCULATION_FINISHED.equals(scoringRequest.getStatus()))
            .build());
  }
}
