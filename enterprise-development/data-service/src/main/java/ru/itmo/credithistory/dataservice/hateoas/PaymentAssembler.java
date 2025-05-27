package ru.itmo.credithistory.dataservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.dataservice.controller.PaymentController;
import ru.itmo.credithistory.dataservice.dto.PaymentDto;
import ru.itmo.credithistory.dataservice.entity.Payment;
import ru.itmo.credithistory.dataservice.mapper.PaymentMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class PaymentAssembler extends EntityToDtoModelAssembler<Payment, PaymentDto> {

  private final PaymentMapper paymentMapper;

  @Override
  protected void addLinks(EntityModel<PaymentDto> entityModel, Payment entity) {
    entityModel.add(
        linkTo(methodOn(PaymentController.class).getPaymentById(entity.getId(), null))
            .withSelfRel());
  }

  @Override
  protected PaymentDto fromEntityToDto(Payment entity) {
    return paymentMapper.fromEntityToDto(entity);
  }
}
