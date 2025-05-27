package ru.itmo.credithistory.dataservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.commons.mapper.EntityMapper;
import ru.itmo.credithistory.dataservice.dto.OverdueDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateOverdueRqDto;
import ru.itmo.credithistory.dataservice.entity.Overdue;

@Mapper(config = BaseMapperConfig.class)
public interface OverdueMapper
    extends EntityMapper<
        Overdue, OverdueDto, CreateOrUpdateOverdueRqDto, CreateOrUpdateOverdueRqDto> {

  @Override
  @Mapping(target = "creditId", source = "credit.id")
  OverdueDto fromEntityToDto(Overdue entity);

  ru.itmo.credithistory.dataservice.grpc.Overdue fromEntityToOverdueGrpc(Overdue entity);
}
