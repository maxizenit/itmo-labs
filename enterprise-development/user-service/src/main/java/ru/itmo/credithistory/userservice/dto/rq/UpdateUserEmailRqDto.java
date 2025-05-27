package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Запрос на обновление e-mail пользователя")
@Data
@Builder
@Jacksonized
public class UpdateUserEmailRqDto {

  @Schema(
      description = "E-mail пользователя",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "example@example.com")
  @NotBlank
  @Email
  private String email;
}
