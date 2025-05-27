package ru.itmo.credithistory.reportservice.configuration;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@ConfigurationProperties("report")
@NullMarked
public class ReportConfigurationProperties {

  private Integer batchSize;
  private Integer parallelism;
  private Duration actualTime;
}
