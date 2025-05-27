package ru.itmo.credithistory.reportservice.mapper;

import org.jetbrains.annotations.Contract;
import org.mapstruct.Mapper;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.reportservice.dto.ReportRequestDto;
import ru.itmo.credithistory.reportservice.entity.ReportRequest;

@Mapper(config = BaseMapperConfig.class)
public interface ReportRequestMapper {

  @Contract(value = "null -> null; !null -> !null", pure = true)
  ReportRequestDto fromEntityToDto(ReportRequest scoringRequest);
}
