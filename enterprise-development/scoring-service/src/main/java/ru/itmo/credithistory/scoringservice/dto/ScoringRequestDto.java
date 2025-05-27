package ru.itmo.credithistory.scoringservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.scoringservice.enm.ScoringRequestStatus;

@Schema(description = "Запрос кредитного рейтинга")
@Data
@Builder
@Jacksonized
public class ScoringRequestDto {

  @Schema(
      description = "Идентификатор пользователя",
      example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID id;

  @Schema(description = "ИНН клиента", example = "123456789012")
  private String customerInn;

  @Schema(description = "Дата рождения", example = "2025-01-01T10:30")
  private LocalDateTime createdAt;

  @Schema(description = "Статус")
  private ScoringRequestStatus status;

  @Schema(description = "Результат (рейтинг)", example = "750")
  private Integer result;
}
