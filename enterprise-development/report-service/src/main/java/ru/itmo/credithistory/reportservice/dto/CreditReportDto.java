package ru.itmo.credithistory.reportservice.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditReportDto {

  private String bankName;
  private String externalId;
  private BigDecimal initialAmount;
  private BigDecimal remainingAmount;
  private String issuedAt;
  private String repaidAt;
  private Boolean active;
  private List<PaymentReportDto> payments;
  private List<OverdueReportDto> overdue;
}
