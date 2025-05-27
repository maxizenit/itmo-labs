package ru.itmo.credithistory.dataservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.dataservice.controller.CreditController;
import ru.itmo.credithistory.dataservice.dto.CreditDto;
import ru.itmo.credithistory.dataservice.entity.Credit;
import ru.itmo.credithistory.dataservice.mapper.CreditMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class CreditAssembler extends EntityToDtoModelAssembler<Credit, CreditDto> {

  private final CreditMapper creditMapper;

  @Override
  protected void addLinks(EntityModel<CreditDto> entityModel, Credit entity) {
    entityModel.add(
        linkTo(methodOn(CreditController.class).getCreditById(entity.getId(), null)).withSelfRel());
    entityModel.add(
        linkTo(methodOn(CreditController.class).getPaymentsByCreditId(entity.getId(), null))
            .withRel("payments"));
    entityModel.add(
        linkTo(methodOn(CreditController.class).getOverdueByCreditId(entity.getId(), null))
            .withRel("overdue"));
  }

  @Override
  protected CreditDto fromEntityToDto(Credit entity) {
    return creditMapper.fromEntityToDto(entity);
  }
}
