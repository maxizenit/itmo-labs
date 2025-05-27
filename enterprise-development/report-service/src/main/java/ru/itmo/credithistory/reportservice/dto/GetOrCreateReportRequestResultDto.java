package ru.itmo.credithistory.reportservice.dto;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import ru.itmo.credithistory.reportservice.entity.ReportRequest;

@Getter
@Builder
@NullMarked
public class GetOrCreateReportRequestResultDto {

  private final ReportRequest reportRequest;
  private final boolean isCreated;
}
