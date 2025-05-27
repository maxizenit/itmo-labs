package ru.itmo.credithistory.dataservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Просрочка")
@Data
@Builder
@Jacksonized
public class OverdueDto {

  @Schema(description = "Идентификатор", example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID id;

  @Schema(description = "Идентификатор кредита", example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID creditId;

  @Schema(description = "Сумма просрочки", example = "25000.0")
  private BigDecimal amount;

  @Schema(description = "Дата и время возникновения", example = "2025-01-01T10:30")
  private LocalDateTime occurredAt;

  @Schema(description = "Дата и время погашения", example = "2025-01-01T10:30")
  private LocalDateTime repaidAt;
}
