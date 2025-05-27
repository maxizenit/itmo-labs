package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Запрос на обновление клиента")
@Data
@Builder
@Jacksonized
public class UpdateCustomerRqDto {

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
