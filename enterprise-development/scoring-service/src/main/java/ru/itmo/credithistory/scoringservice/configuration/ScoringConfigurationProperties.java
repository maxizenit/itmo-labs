package ru.itmo.credithistory.scoringservice.configuration;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@ConfigurationProperties("scoring")
@NullMarked
public class ScoringConfigurationProperties {

  private Integer creditApplicationsActualPeriodDays;
  private Double minOverdueAmount;
  private Integer batchSize;
  private Integer parallelism;
  private Duration actualTime;
}
