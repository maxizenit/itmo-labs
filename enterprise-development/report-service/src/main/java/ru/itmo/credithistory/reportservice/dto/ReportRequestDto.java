package ru.itmo.credithistory.reportservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.reportservice.enm.ReportRequestStatus;

@Schema(description = "Клиент")
@Data
@Builder
@Jacksonized
public class ReportRequestDto {

  private UUID id;
  private String customerInn;
  private LocalDateTime createdAt;
  private ReportRequestStatus status;
}
