package ru.itmo.credithistory.scoringservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.itmo.credithistory.dataservice.grpc.CustomerStatisticsRequest;
import ru.itmo.credithistory.dataservice.grpc.CustomerStatisticsResponse;
import ru.itmo.credithistory.dataservice.grpc.CustomerStatisticsResponseOverdue;
import ru.itmo.credithistory.dataservice.grpc.DataServiceGrpc;
import ru.itmo.credithistory.scoringservice.configuration.ScoringConfigurationProperties;

@Slf4j
@Service
@RequiredArgsConstructor
@NullMarked
public class CalculationService {

  private final DataServiceGrpc.DataServiceBlockingStub dataServiceBlockingStub;
  private final ScoringConfigurationProperties scoringConfigurationProperties;

  public int calculateCreditRating(String customerInn) {
    log.info("Started calculating credit rating for customerInn: {}", customerInn);

    CustomerStatisticsResponse response =
        dataServiceBlockingStub.getCustomerStatistics(
            CustomerStatisticsRequest.newBuilder()
                .setCustomerInn(customerInn)
                .setCreditApplicationsPeriodDays(
                    scoringConfigurationProperties.getCreditApplicationsActualPeriodDays())
                .setMinOverdueAmount(
                    scoringConfigurationProperties.getMinOverdueAmount().toString())
                .build());

    int debtScore =
        calculateDebtScore(
            new BigDecimal(response.getTotalInitialAmountForActiveCredits()),
            new BigDecimal(response.getTotalRemainingAmountForActiveCredits()));
    log.info("Debt score for customerInn: {}", debtScore);

    int activityScore =
        calculateActivityScore(response.getActiveCreditsCount(), response.getClosedCreditsCount());
    log.info("Activity score for customerInn: {}", activityScore);

    int applicationScore =
        calculateApplicationsScore(response.getCreditApplicationsByLastPeriodCount());
    log.info("Application score for customerInn: {}", applicationScore);

    int overdueScore = calculateOverdueScore(response.getOverdueList());
    log.info("Overdue score for customerInn: {}", overdueScore);

    int scoreSum = debtScore + activityScore + applicationScore + overdueScore;

    int rating = Math.min(1000, Math.max(0, scoreSum));
    log.info("Credit rating for customerInn: {}", rating);
    return rating;
  }

  private int calculateDebtScore(
      BigDecimal totalInitialAmountForActiveCredits,
      BigDecimal totalRemainingAmountForActiveCredits) {
    double utilization =
        totalInitialAmountForActiveCredits.compareTo(BigDecimal.ZERO) > 0
            ? totalRemainingAmountForActiveCredits
                .divide(totalInitialAmountForActiveCredits, RoundingMode.HALF_UP)
                .doubleValue()
            : 0;

    int debtScore = 0;
    if (utilization <= 0.3) debtScore = 350;
    else if (utilization <= 0.5) {
      debtScore = 300;
    } else if (utilization <= 0.7) {
      debtScore = 200;
    } else if (utilization <= 0.9) {
      debtScore = 100;
    }
    return debtScore;
  }

  private int calculateActivityScore(int activeCreditsCount, int closedCreditsCount) {
    return Math.min(150, activeCreditsCount * 30) + Math.min(100, closedCreditsCount * 20);
  }

  private int calculateApplicationsScore(int creditApplicationsByLastPeriodCount) {
    int applicationScore = 0;
    if (creditApplicationsByLastPeriodCount <= 1) {
      applicationScore = 150;
    } else if (creditApplicationsByLastPeriodCount <= 3) {
      applicationScore = 100;
    } else if (creditApplicationsByLastPeriodCount <= 5) {
      applicationScore = 50;
    }
    return applicationScore;
  }

  private int calculateOverdueScore(List<CustomerStatisticsResponseOverdue> overdueList) {
    long totalPenaltyPoints = 0;
    for (CustomerStatisticsResponseOverdue overdue : overdueList) {
      long amount = new BigDecimal(overdue.getAmount()).longValue();
      totalPenaltyPoints += amount * overdue.getDays();
    }

    int overdueScore = 0;
    if (totalPenaltyPoints == 0) {
      overdueScore = 250;
    } else if (totalPenaltyPoints < 50000) {
      overdueScore = 180;
    } else if (totalPenaltyPoints < 200000) {
      overdueScore = 100;
    }
    return overdueScore;
  }
}
