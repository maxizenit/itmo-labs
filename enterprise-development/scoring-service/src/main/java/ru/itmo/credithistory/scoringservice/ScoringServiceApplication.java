package ru.itmo.credithistory.scoringservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.itmo.credithistory.scoringservice.configuration.ScoringConfigurationProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ScoringConfigurationProperties.class)
public class ScoringServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ScoringServiceApplication.class, args);
  }
}
