package ru.itmo.credithistory.commons.kafka.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ReportServiceScoringResponseDto {

  private UUID reportRequestId;
  private Integer result;
  private Boolean success;
}
