package ru.itmo.credithistory.userservice.configuration;

import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@AllArgsConstructor
@ConfigurationProperties("jwt")
@NullMarked
public class JwtConfigurationProperties {

  private String secret;
  private Duration lifetime;
}
