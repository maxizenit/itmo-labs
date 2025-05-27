package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Запрос на аутентификацию пользователя")
@Data
@Builder
@Jacksonized
public class LoginUserRqDto {

  @Schema(
      description = "E-mail пользователя",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "example@example.com")
  @NotBlank
  @Email
  private String email;

  @Schema(
      description = "Пароль пользователя",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "password")
  @NotBlank
  private String password;
}
