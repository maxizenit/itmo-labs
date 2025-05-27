package ru.itmo.credithistory.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.itmo.credithistory.commons.kafka.KafkaTopics;
import ru.itmo.credithistory.commons.kafka.dto.UserRegistrationNotificationDto;

@Slf4j
@Service
public class UserRegistrationNotificationService {

  @KafkaListener(topics = KafkaTopics.USER_REGISTRATION_NOTIFICATION)
  public void sendUserPassword(UserRegistrationNotificationDto userRegistrationNotificationDto) {
    log.info(
        "Send password {} for user with email {}",
        userRegistrationNotificationDto.getPassword(),
        userRegistrationNotificationDto.getEmail());
  }
}
