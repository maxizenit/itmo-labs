package ru.itmo.credithistory.userservice.mapper;

import org.jetbrains.annotations.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.commons.mapper.EntityMapper;
import ru.itmo.credithistory.userservice.dto.CreditOrganizationDto;
import ru.itmo.credithistory.userservice.dto.rq.CreateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;

@Mapper(config = BaseMapperConfig.class)
public interface CreditOrganizationMapper
    extends EntityMapper<
        CreditOrganization,
        CreditOrganizationDto,
        CreateCreditOrganizationRqDto,
        UpdateCreditOrganizationRqDto> {

  @Mapping(target = "id", source = "entity.userId")
  @Contract(value = "null -> null; !null -> !null", pure = true)
  ru.itmo.credithistory.userservice.grpc.CreditOrganization fromEntityToCreditOrganizationGrpc(
      CreditOrganization entity);
}
