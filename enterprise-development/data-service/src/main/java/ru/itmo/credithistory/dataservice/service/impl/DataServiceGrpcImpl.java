package ru.itmo.credithistory.dataservice.service.impl;

import io.grpc.stub.StreamObserver;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.itmo.credithistory.dataservice.entity.Credit;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;
import ru.itmo.credithistory.dataservice.entity.Overdue;
import ru.itmo.credithistory.dataservice.grpc.*;
import ru.itmo.credithistory.dataservice.mapper.CreditApplicationMapper;
import ru.itmo.credithistory.dataservice.mapper.CreditMapper;
import ru.itmo.credithistory.dataservice.mapper.OverdueMapper;
import ru.itmo.credithistory.dataservice.mapper.PaymentMapper;
import ru.itmo.credithistory.dataservice.service.CreditApplicationService;
import ru.itmo.credithistory.dataservice.service.CreditService;

@Service
@RequiredArgsConstructor
@NullMarked
public class DataServiceGrpcImpl extends DataServiceGrpc.DataServiceImplBase {

  private final CreditService creditService;
  private final CreditApplicationService creditApplicationService;
  private final CreditMapper creditMapper;
  private final CreditApplicationMapper creditApplicationMapper;
  private final PaymentMapper paymentMapper;
  private final OverdueMapper overdueMapper;

  @Override
  public void getCustomerStatistics(
      CustomerStatisticsRequest request,
      StreamObserver<CustomerStatisticsResponse> responseObserver) {
    LocalDateTime now = LocalDateTime.now();

    BigDecimal minOverdueAmount = new BigDecimal(request.getMinOverdueAmount());

    List<Credit> credits = creditService.getAllCustomerCreditsWithOverdue(request.getCustomerInn());
    List<Overdue> overdue =
        credits.stream()
            .flatMap(c -> c.getOverdue().stream())
            .filter(o -> o.getAmount().compareTo(minOverdueAmount) >= 0)
            .toList();

    List<Credit> activeCredits = credits.stream().filter(Credit::getActive).toList();
    List<Credit> closedCredits = credits.stream().filter(c -> !c.getActive()).toList();

    BigDecimal totalInitialAmountForActiveCredits =
        activeCredits.stream()
            .map(Credit::getInitialAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal totalRemainingAmountForActiveCredits =
        activeCredits.stream()
            .map(Credit::getRemainingAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    List<CreditApplication> creditApplications =
        creditApplicationService.getAllCustomerCreditApplications(
            request.getCustomerInn(), now.minusDays(request.getCreditApplicationsPeriodDays()));

    CustomerStatisticsResponse response =
        CustomerStatisticsResponse.newBuilder()
            .setTotalInitialAmountForActiveCredits(totalInitialAmountForActiveCredits.toString())
            .setTotalRemainingAmountForActiveCredits(
                totalRemainingAmountForActiveCredits.toString())
            .setActiveCreditsCount(activeCredits.size())
            .setClosedCreditsCount(closedCredits.size())
            .setCreditApplicationsByLastPeriodCount(creditApplications.size())
            .addAllOverdue(
                overdue.stream()
                    .map(
                        o ->
                            CustomerStatisticsResponseOverdue.newBuilder()
                                .setAmount(o.getAmount().toString())
                                .setDays(getOverdueDays(o, now))
                                .build())
                    .toList())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getCustomerData(
      GetCustomerDataRequest request, StreamObserver<GetCustomerDataResponse> responseObserver) {
    List<Credit> credits =
        creditService.getAllCustomerCreditsWithPaymentsAndOverdue(request.getCustomerInn());
    List<CreditApplication> creditApplications =
        creditApplicationService.getAllCustomerCreditApplications(request.getCustomerInn(), null);

    GetCustomerDataResponse response =
        GetCustomerDataResponse.newBuilder()
            .addAllCredits(
                credits.stream()
                    .map(
                        credit -> {
                          ru.itmo.credithistory.dataservice.grpc.Credit creditGrpc =
                              creditMapper.fromEntityToCreditGrpc(credit);
                          return creditGrpc.toBuilder()
                              .addAllPayments(
                                  credit.getPayments().stream()
                                      .map(paymentMapper::fromEntityToPaymentGrpc)
                                      .toList())
                              .addAllOverdue(
                                  credit.getOverdue().stream()
                                      .map(overdueMapper::fromEntityToOverdueGrpc)
                                      .toList())
                              .build();
                        })
                    .toList())
            .addAllCreditApplications(
                creditApplications.stream()
                    .map(creditApplicationMapper::fromEntityToCreditApplicationGrpc)
                    .toList())
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private int getOverdueDays(Overdue overdue, LocalDateTime now) {
    return (int)
        ChronoUnit.DAYS.between(
            overdue.getOccurredAt(), overdue.getRepaidAt() != null ? overdue.getRepaidAt() : now);
  }
}
