package ru.itmo.credithistory.dataservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.dataservice.controller.CreditApplicationController;
import ru.itmo.credithistory.dataservice.dto.CreditApplicationDto;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;
import ru.itmo.credithistory.dataservice.mapper.CreditApplicationMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class CreditApplicationAssembler
    extends EntityToDtoModelAssembler<CreditApplication, CreditApplicationDto> {

  private final CreditApplicationMapper creditApplicationMapper;

  @Override
  protected void addLinks(EntityModel<CreditApplicationDto> entityModel, CreditApplication entity) {
    entityModel.add(
        linkTo(
                methodOn(CreditApplicationController.class)
                    .getCreditApplicationById(entity.getId(), null))
            .withSelfRel());
  }

  @Override
  protected CreditApplicationDto fromEntityToDto(CreditApplication entity) {
    return creditApplicationMapper.fromEntityToDto(entity);
  }
}
