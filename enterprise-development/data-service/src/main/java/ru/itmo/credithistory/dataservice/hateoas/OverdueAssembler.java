package ru.itmo.credithistory.dataservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.dataservice.controller.OverdueController;
import ru.itmo.credithistory.dataservice.dto.OverdueDto;
import ru.itmo.credithistory.dataservice.entity.Overdue;
import ru.itmo.credithistory.dataservice.mapper.OverdueMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class OverdueAssembler extends EntityToDtoModelAssembler<Overdue, OverdueDto> {

  private final OverdueMapper overdueMapper;

  @Override
  protected void addLinks(EntityModel<OverdueDto> entityModel, Overdue entity) {
    entityModel.add(
        linkTo(methodOn(OverdueController.class).getOverdueById(entity.getId(), null))
            .withSelfRel());
  }

  @Override
  protected OverdueDto fromEntityToDto(Overdue entity) {
    return overdueMapper.fromEntityToDto(entity);
  }
}
