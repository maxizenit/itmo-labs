package ru.itmo.credithistory.commons.kafka.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class UserRegistrationNotificationDto {

  private String email;
  private String password;
}
