package ru.itmo.credithistory.userservice.mapper;

import org.mapstruct.Mapper;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.commons.mapper.EntityMapper;
import ru.itmo.credithistory.userservice.dto.EmployeeDto;
import ru.itmo.credithistory.userservice.dto.rq.CreateEmployeeRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateEmployeeRqDto;
import ru.itmo.credithistory.userservice.entity.Employee;

@Mapper(config = BaseMapperConfig.class)
public interface EmployeeMapper
    extends EntityMapper<Employee, EmployeeDto, CreateEmployeeRqDto, UpdateEmployeeRqDto> {}
