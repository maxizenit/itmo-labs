package ru.itmo.credithistory.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "JWT-токен аутентификации")
@Data
@Builder
@Jacksonized
public class TokenDto {

  @Schema(description = "JWT-токен аутентификации")
  private String token;
}
