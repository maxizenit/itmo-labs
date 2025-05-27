package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Запрос на обновление пароля пользователя")
@Data
@Builder
@Jacksonized
public class UpdateUserPasswordRqDto {

  @Schema(
      description = "Пароль пользователя",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "newpassword")
  @NotBlank
  private String password;
}
