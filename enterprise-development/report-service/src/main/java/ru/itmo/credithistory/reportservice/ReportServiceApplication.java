package ru.itmo.credithistory.reportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.itmo.credithistory.reportservice.configuration.ReportConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ReportConfigurationProperties.class)
public class ReportServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ReportServiceApplication.class, args);
  }
}
