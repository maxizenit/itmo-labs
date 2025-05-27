package ru.itmo.credithistory.dataservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.dataservice.enm.CreditApplicationStatus;
import ru.itmo.credithistory.dataservice.enm.CreditType;

@Schema(description = "Запрос на создание или обновление кредитной заявки")
@Data
@Builder
@Jacksonized
public class CreateOrUpdateCreditApplicationRqDto {

  @Schema(description = "ИНН клиента", example = "123456789012")
  @NotBlank
  @Pattern(regexp = "^\\d{12}")
  private String customerInn;

  @Schema(description = "Тип кредита")
  @NotNull
  private CreditType creditType;

  @Schema(description = "Запрашиваемая сумма", example = "25000.0")
  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal amount;

  @Schema(description = "Дата и время создания", example = "2025-01-01T10:30")
  @NotNull
  @PastOrPresent
  private LocalDateTime createdAt;

  @Schema(description = "Статус")
  @NotNull
  private CreditApplicationStatus status;
}
