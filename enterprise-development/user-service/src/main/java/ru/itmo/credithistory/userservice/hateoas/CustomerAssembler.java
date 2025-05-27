package ru.itmo.credithistory.userservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.userservice.controller.CustomerController;
import ru.itmo.credithistory.userservice.dto.CustomerDto;
import ru.itmo.credithistory.userservice.entity.Customer;
import ru.itmo.credithistory.userservice.mapper.CustomerMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class CustomerAssembler extends EntityToDtoModelAssembler<Customer, CustomerDto> {

  private final CustomerMapper customerMapper;

  @Override
  protected void addLinks(EntityModel<CustomerDto> entityModel, Customer entity) {
    entityModel.add(
        linkTo(methodOn(CustomerController.class).getCustomerByUserId(entity.getUserId()))
            .withSelfRel());
    entityModel.add(
        linkTo(methodOn(CustomerController.class).getCustomerByInn(entity.getInn()))
            .withRel("by-inn"));
  }

  @Override
  protected CustomerDto fromEntityToDto(Customer entity) {
    return customerMapper.fromEntityToDto(entity);
  }
}
