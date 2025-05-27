package ru.itmo.credithistory.scoringservice.mapper;

import org.jetbrains.annotations.Contract;
import org.mapstruct.Mapper;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.scoringservice.dto.ScoringRequestDto;
import ru.itmo.credithistory.scoringservice.entity.ScoringRequest;

@Mapper(config = BaseMapperConfig.class)
public interface ScoringRequestMapper {

  @Contract(value = "null -> null; !null -> !null", pure = true)
  ScoringRequestDto fromEntityToDto(ScoringRequest entity);
}
