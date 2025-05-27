package ru.itmo.credithistory.commons.kafka.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ReportServiceScoringRequestDto {

  private String customerInn;
  private UUID reportRequestId;
}
