package ru.itmo.credithistory.reportservice.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDataDto {

  private Integer creditRating;
  private List<CreditReportDto> credits;
  private List<CreditApplicationReportDto> creditApplications;
}
