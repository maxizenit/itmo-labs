package ru.itmo.credithistory.userservice.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@ConfigurationProperties("admin")
@NullMarked
public class AdminConfigurationProperties {

  private String email;
}
