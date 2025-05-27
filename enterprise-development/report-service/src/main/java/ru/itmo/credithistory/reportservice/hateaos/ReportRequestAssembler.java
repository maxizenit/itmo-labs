package ru.itmo.credithistory.reportservice.hateaos;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.reportservice.controller.ReportRequestController;
import ru.itmo.credithistory.reportservice.dto.ReportRequestDto;
import ru.itmo.credithistory.reportservice.entity.ReportRequest;
import ru.itmo.credithistory.reportservice.mapper.ReportRequestMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class ReportRequestAssembler
    extends EntityToDtoModelAssembler<ReportRequest, ReportRequestDto> {

  private final ReportRequestMapper reportRequestMapper;

  @Override
  protected void addLinks(EntityModel<ReportRequestDto> entityModel, ReportRequest entity) {
    entityModel.add(
        linkTo(
                methodOn(ReportRequestController.class)
                    .getOrCreateReportRequestByCustomerInn(
                        entity.getCustomerInn(), entity.getCustomerInn()))
            .withSelfRel());
    entityModel.add(
        linkTo(
                methodOn(ReportRequestController.class)
                    .getReportByCustomerInn(entity.getCustomerInn(), entity.getCustomerInn()))
            .withRel("report"));
  }

  @Override
  protected ReportRequestDto fromEntityToDto(ReportRequest entity) {
    return reportRequestMapper.fromEntityToDto(entity);
  }
}
