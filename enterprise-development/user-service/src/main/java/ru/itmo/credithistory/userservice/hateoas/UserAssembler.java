package ru.itmo.credithistory.userservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.userservice.controller.UserController;
import ru.itmo.credithistory.userservice.dto.UserDto;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.mapper.UserMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class UserAssembler extends EntityToDtoModelAssembler<User, UserDto> {

  private final UserMapper userMapper;

  @Override
  protected void addLinks(EntityModel<UserDto> entityModel, User entity) {
    entityModel.add(
        linkTo(methodOn(UserController.class).getUserById(entity.getId())).withSelfRel());
  }

  @Override
  protected UserDto fromEntityToDto(User entity) {
    return userMapper.fromEntityToDto(entity);
  }
}
