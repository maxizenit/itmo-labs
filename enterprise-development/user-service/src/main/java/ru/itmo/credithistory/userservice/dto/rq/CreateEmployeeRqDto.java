package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.userservice.enm.UserRole;

@Schema(description = "Запрос на создание сотрудника")
@Data
@Builder
@Jacksonized
public class CreateEmployeeRqDto {

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

  @Schema(
      description = "Роль пользователя",
      requiredMode = Schema.RequiredMode.REQUIRED,
      allowableValues = {"SUPERVISOR", "EMPLOYEE"})
  @NotNull
  private UserRole role;

  @Schema(description = "Фамилия", requiredMode = Schema.RequiredMode.REQUIRED, example = "Иванов")
  @NotBlank
  @Pattern(regexp = "^[А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)?$")
  private String lastName;

  @Schema(description = "Имя", requiredMode = Schema.RequiredMode.REQUIRED, example = "Иван")
  @NotBlank
  @Pattern(regexp = "^[А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)?$")
  private String firstName;

  @Schema(
      description = "Отчество",
      requiredMode = Schema.RequiredMode.NOT_REQUIRED,
      example = "Иванович")
  @Pattern(regexp = "^[А-ЯЁ][а-яё]+(-[А-ЯЁ][а-яё]+)?$")
  private String middleName;
}
