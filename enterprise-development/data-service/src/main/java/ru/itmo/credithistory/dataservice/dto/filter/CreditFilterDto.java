package ru.itmo.credithistory.dataservice.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditFilterDto {

  private String customerInn;
  private Boolean active;
}
