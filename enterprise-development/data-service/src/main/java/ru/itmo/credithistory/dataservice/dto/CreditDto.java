package ru.itmo.credithistory.dataservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.dataservice.enm.CreditType;

@Schema(description = "Кредит")
@Data
@Builder
@Jacksonized
public class CreditDto {

  @Schema(description = "Идентификатор", example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID id;

  @Schema(description = "Внешний идентификатор", example = "ext-id-123")
  private String externalId;

  @Schema(description = "ИНН клиента", example = "123456789012")
  private String customerInn;

  @Schema(
      description = "Идентификатор кредитной организации",
      example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID creditOrganizationId;

  @Schema(description = "Тип кредита")
  private CreditType type;

  @Schema(description = "Начальная сумма", example = "25000.0")
  private BigDecimal initialAmount;

  @Schema(description = "Оставшаяся к выплате сумма", example = "25000.0")
  private BigDecimal remainingAmount;

  @Schema(description = "Дата и время выдачи", example = "2025-01-01T10:30")
  private LocalDateTime issuedAt;

  @Schema(description = "Дата и время погашения", example = "2025-01-01T10:30")
  private LocalDateTime repaidAt;

  @Schema(description = "Активен ли")
  private Boolean active;
}
