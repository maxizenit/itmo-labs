package ru.itmo.credithistory.dataservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.commons.mapper.EntityMapper;
import ru.itmo.credithistory.dataservice.dto.PaymentDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdatePaymentRqDto;
import ru.itmo.credithistory.dataservice.entity.Payment;

@Mapper(config = BaseMapperConfig.class)
public interface PaymentMapper
    extends EntityMapper<
        Payment, PaymentDto, CreateOrUpdatePaymentRqDto, CreateOrUpdatePaymentRqDto> {

  @Override
  @Mapping(target = "creditId", source = "credit.id")
  PaymentDto fromEntityToDto(Payment entity);

  ru.itmo.credithistory.dataservice.grpc.Payment fromEntityToPaymentGrpc(Payment entity);
}
