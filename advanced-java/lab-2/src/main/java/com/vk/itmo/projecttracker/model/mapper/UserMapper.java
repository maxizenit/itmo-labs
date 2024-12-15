package com.vk.itmo.projecttracker.model.mapper;

import com.vk.itmo.projecttracker.model.dto.UserDto;
import com.vk.itmo.projecttracker.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(source = "id", target = "userId")
    UserDto fromUserToUserDto(User user);
}
