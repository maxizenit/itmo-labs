package ru.itmo.credithistory.userservice.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.userservice.configuration.AdminConfigurationProperties;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
@NullMarked
public class AdminInitializer implements CommandLineRunner {

  private final UserService userService;
  private final AdminConfigurationProperties adminConfigurationProperties;

  @Override
  public void run(String... args) {
    String email = adminConfigurationProperties.getEmail();
    if (userService.adminRegistered()) {
      return;
    }
    String randomPassword = RandomStringUtils.secure().nextAlphanumeric(10);
    userService.createUser(email, randomPassword, UserRole.ADMIN);
    log.warn("Registered admin with password (change it!): {}", randomPassword);
  }
}
