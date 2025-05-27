package ru.itmo.credithistory.userservice.mapper;

import org.jetbrains.annotations.Contract;
import org.mapstruct.Mapper;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.userservice.dto.UserDto;
import ru.itmo.credithistory.userservice.entity.User;

@Mapper(config = BaseMapperConfig.class)
public interface UserMapper {

  @Contract(value = "null -> null; !null -> !null", pure = true)
  UserDto fromEntityToDto(User user);
}
