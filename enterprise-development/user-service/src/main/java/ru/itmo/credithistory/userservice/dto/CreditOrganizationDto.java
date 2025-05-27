package ru.itmo.credithistory.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.userservice.enm.CreditOrganizationType;

@Schema(description = "Кредитная организация")
@Data
@Builder
@Jacksonized
public class CreditOrganizationDto {

  @Schema(
      description = "Идентификатор пользователя",
      example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID userId;

  @Schema(description = "ИНН", example = "1234567890")
  private String inn;

  @Schema(description = "Краткое название", example = "МиниБанк")
  private String shortName;

  @Schema(description = "Полное название", example = "Акционерное Общество \"МиниБанк\"")
  private String fullName;

  @Schema(description = "Тип")
  private CreditOrganizationType type;
}
