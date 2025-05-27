package ru.itmo.credithistory.reportservice.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditApplicationReportDto {

  private String bankName;
  private BigDecimal amount;
  private String createdAt;
}
