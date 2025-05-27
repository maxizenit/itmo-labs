package ru.itmo.credithistory.scoringservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.scoringservice.controller.ScoringRequestController;
import ru.itmo.credithistory.scoringservice.dto.ScoringRequestDto;
import ru.itmo.credithistory.scoringservice.entity.ScoringRequest;
import ru.itmo.credithistory.scoringservice.mapper.ScoringRequestMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class ScoringRequestAssembler
    extends EntityToDtoModelAssembler<ScoringRequest, ScoringRequestDto> {

  private final ScoringRequestMapper scoringRequestMapper;

  @Override
  protected void addLinks(EntityModel<ScoringRequestDto> entityModel, ScoringRequest entity) {
    entityModel.add(
        linkTo(
                methodOn(ScoringRequestController.class)
                    .getOrCreateScoringRequestByCustomerInn(
                        entity.getCustomerInn(), entity.getCustomerInn()))
            .withSelfRel());
  }

  @Override
  protected ScoringRequestDto fromEntityToDto(ScoringRequest entity) {
    return scoringRequestMapper.fromEntityToDto(entity);
  }
}
