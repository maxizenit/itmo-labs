package ru.itmo.credithistory.dataservice.mapper;

import org.mapstruct.Mapper;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.commons.mapper.EntityMapper;
import ru.itmo.credithistory.dataservice.dto.CreditApplicationDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditApplicationRqDto;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;

@Mapper(config = BaseMapperConfig.class)
public interface CreditApplicationMapper
    extends EntityMapper<
        CreditApplication,
        CreditApplicationDto,
        CreateOrUpdateCreditApplicationRqDto,
        CreateOrUpdateCreditApplicationRqDto> {

  ru.itmo.credithistory.dataservice.grpc.CreditApplication fromEntityToCreditApplicationGrpc(
      CreditApplication entity);
}
