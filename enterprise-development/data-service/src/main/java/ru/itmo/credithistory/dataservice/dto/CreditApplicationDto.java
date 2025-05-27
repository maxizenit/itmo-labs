package ru.itmo.credithistory.dataservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.dataservice.enm.CreditApplicationStatus;
import ru.itmo.credithistory.dataservice.enm.CreditType;

@Schema(description = "Кредитная заявка")
@Data
@Builder
@Jacksonized
public class CreditApplicationDto {

  @Schema(description = "Идентификатор", example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID id;

  @Schema(description = "ИНН клиента", example = "123456789012")
  private String customerInn;

  @Schema(
      description = "Идентификатор кредитной организации",
      example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID creditOrganizationId;

  @Schema(description = "Тип кредита")
  private CreditType creditType;

  @Schema(description = "Запрашиваемая сумма", example = "25000.0")
  private BigDecimal amount;

  @Schema(description = "Дата и время создания", example = "2025-01-01T10:30")
  private LocalDateTime createdAt;

  @Schema(description = "Статус")
  private CreditApplicationStatus status;
}
