package ru.itmo.credithistory.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.itmo.credithistory.userservice.configuration.AdminConfigurationProperties;
import ru.itmo.credithistory.userservice.configuration.JwtConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
  AdminConfigurationProperties.class,
  JwtConfigurationProperties.class
})
public class UserServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }
}
