package ru.itmo.credithistory.dataservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.dataservice.enm.CreditType;

@Schema(description = "Запрос на создание или обновление кредита")
@Data
@Builder
@Jacksonized
public class CreateOrUpdateCreditRqDto {

  @Schema(description = "Внешний идентификатор", example = "ext-id-123")
  @NotBlank
  private String externalId;

  @Schema(description = "ИНН клиента", example = "123456789012")
  @NotBlank
  @Pattern(regexp = "^\\d{12}")
  private String customerInn;

  @Schema(description = "Тип кредита")
  @NotNull
  private CreditType type;

  @Schema(description = "Начальная сумма", example = "25000.0")
  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal initialAmount;

  @Schema(description = "Оставшаяся к выплате сумма", example = "25000.0")
  @NotNull
  @DecimalMin("0.0")
  private BigDecimal remainingAmount;

  @Schema(description = "Дата и время выдачи", example = "2025-01-01T10:30")
  @NotNull
  @PastOrPresent
  private LocalDateTime issuedAt;

  @Schema(description = "Дата и время погашения", example = "2025-01-01T10:30")
  @PastOrPresent
  private LocalDateTime repaidAt;

  @Schema(description = "Активен ли")
  @NotNull
  private Boolean active;
}
