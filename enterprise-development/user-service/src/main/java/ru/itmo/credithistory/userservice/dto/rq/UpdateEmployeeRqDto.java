package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Запрос на обновление сотрудника")
@Data
@Builder
@Jacksonized
public class UpdateEmployeeRqDto {

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
