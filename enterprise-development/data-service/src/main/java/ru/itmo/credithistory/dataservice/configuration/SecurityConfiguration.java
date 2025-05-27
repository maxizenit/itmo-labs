package ru.itmo.credithistory.dataservice.configuration;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.itmo.credithistory.commons.security.GatewayAuthFilter;

@Configuration
@Import(GatewayAuthFilter.class)
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@NullMarked
public class SecurityConfiguration {

  private final GatewayAuthFilter gatewayAuthFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            c -> {
              c.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
                  .permitAll();
              c.requestMatchers("/actuator/health", "/actuator/info").permitAll();
              c.requestMatchers("/actuator/**").hasRole("ADMIN");
              c.anyRequest().authenticated();
            })
        .addFilterBefore(gatewayAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
