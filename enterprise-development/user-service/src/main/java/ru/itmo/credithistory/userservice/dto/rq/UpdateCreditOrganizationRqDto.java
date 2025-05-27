package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.userservice.enm.CreditOrganizationType;

@Schema(description = "Запрос на обновление кредитной организации")
@Data
@Builder
@Jacksonized
public class UpdateCreditOrganizationRqDto {

  @Schema(description = "ИНН", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234567890")
  @NotBlank
  @Pattern(regexp = "^\\d{10}")
  private String inn;

  @Schema(
      description = "Краткое название",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "МиниБанк")
  @NotBlank
  @Pattern(regexp = "^[А-ЯЁа-яё0-9\"«».,()\\- ]{2,100}$")
  private String shortName;

  @Schema(
      description = "Полное название",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "Акционерное Общество \"МиниБанк\"")
  @NotBlank
  @Pattern(regexp = "^[А-ЯЁа-яё0-9\"«».,()\\- ]{2,100}$")
  private String fullName;

  @Schema(
      description = "Тип",
      requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull
  private CreditOrganizationType type;
}
