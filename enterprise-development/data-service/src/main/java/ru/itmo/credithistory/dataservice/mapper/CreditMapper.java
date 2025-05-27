package ru.itmo.credithistory.dataservice.mapper;

import org.mapstruct.Mapper;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.commons.mapper.EntityMapper;
import ru.itmo.credithistory.dataservice.dto.CreditDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditRqDto;
import ru.itmo.credithistory.dataservice.entity.Credit;

@Mapper(config = BaseMapperConfig.class)
public interface CreditMapper
    extends EntityMapper<Credit, CreditDto, CreateOrUpdateCreditRqDto, CreateOrUpdateCreditRqDto> {

  ru.itmo.credithistory.dataservice.grpc.Credit fromEntityToCreditGrpc(Credit entity);
}
