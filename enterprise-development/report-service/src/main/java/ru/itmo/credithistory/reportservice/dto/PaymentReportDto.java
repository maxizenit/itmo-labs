package ru.itmo.credithistory.reportservice.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentReportDto {

  private BigDecimal amount;
  private String paidAt;
}
