package ru.itmo.credithistory.commons.kafka;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@NullMarked
public class KafkaTopics {

  public static final String USER_REGISTRATION_NOTIFICATION = "user-registration-notification";
  public static final String REPORT_SERVICE_SCORING_REQUEST = "report-service-scoring-request";
  public static final String REPORT_SERVICE_SCORING_RESPONSE = "report-service-scoring-response";
}
