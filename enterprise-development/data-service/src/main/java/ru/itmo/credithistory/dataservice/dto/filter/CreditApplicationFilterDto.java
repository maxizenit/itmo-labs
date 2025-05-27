package ru.itmo.credithistory.dataservice.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditApplicationFilterDto {

  private String customerInn;
  private LocalDateTime createdAtFrom;
}
