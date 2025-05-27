package ru.itmo.credithistory.userservice.mapper;

import org.mapstruct.Mapper;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.commons.mapper.EntityMapper;
import ru.itmo.credithistory.userservice.dto.CustomerDto;
import ru.itmo.credithistory.userservice.dto.rq.CreateCustomerRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCustomerRqDto;
import ru.itmo.credithistory.userservice.entity.Customer;

@Mapper(config = BaseMapperConfig.class)
public interface CustomerMapper
    extends EntityMapper<Customer, CustomerDto, CreateCustomerRqDto, UpdateCustomerRqDto> {}
