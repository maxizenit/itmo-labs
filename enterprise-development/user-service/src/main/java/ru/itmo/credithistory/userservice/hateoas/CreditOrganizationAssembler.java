package ru.itmo.credithistory.userservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.userservice.controller.CreditOrganizationController;
import ru.itmo.credithistory.userservice.controller.UserController;
import ru.itmo.credithistory.userservice.dto.CreditOrganizationDto;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;
import ru.itmo.credithistory.userservice.mapper.CreditOrganizationMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class CreditOrganizationAssembler
    extends EntityToDtoModelAssembler<CreditOrganization, CreditOrganizationDto> {

  private final CreditOrganizationMapper creditOrganizationMapper;

  @Override
  protected void addLinks(
      EntityModel<CreditOrganizationDto> entityModel, CreditOrganization entity) {
    entityModel.add(
        linkTo(
                methodOn(CreditOrganizationController.class)
                    .getCreditOrganizationByUserId(entity.getUserId()))
            .withSelfRel());
    entityModel.add(
        linkTo(methodOn(UserController.class).getUserById(entity.getUserId())).withRel("user"));
  }

  @Override
  protected CreditOrganizationDto fromEntityToDto(CreditOrganization entity) {
    return creditOrganizationMapper.fromEntityToDto(entity);
  }
}
