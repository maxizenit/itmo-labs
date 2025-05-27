package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Запрос на создание клиента")
@Data
@Builder
@Jacksonized
public class CreateCustomerRqDto {

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
      description = "ИНН",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "123456789012")
  @NotBlank
  @Pattern(regexp = "^\\d{12}")
  private String inn;

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

  @Schema(
      description = "Дата рождения",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "2000-01-01")
  @NotNull
  @Past
  private LocalDate birthdate;
}
