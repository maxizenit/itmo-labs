package ru.itmo.credithistory.dataservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Запрос на создание или обновление платежа")
@Data
@Builder
@Jacksonized
public class CreateOrUpdatePaymentRqDto {

  @Schema(description = "Сумма платежа", example = "25000.0")
  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  BigDecimal amount;

  @Schema(description = "Дата и время оплаты", example = "2025-01-01T10:30")
  @NotNull
  @PastOrPresent
  private LocalDateTime paidAt;
}
