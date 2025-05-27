package ru.itmo.credithistory.userservice.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.hateoas.EntityToDtoModelAssembler;
import ru.itmo.credithistory.userservice.controller.EmployeeController;
import ru.itmo.credithistory.userservice.dto.EmployeeDto;
import ru.itmo.credithistory.userservice.entity.Employee;
import ru.itmo.credithistory.userservice.mapper.EmployeeMapper;

@Component
@RequiredArgsConstructor
@NullMarked
public class EmployeeAssembler extends EntityToDtoModelAssembler<Employee, EmployeeDto> {

  private final EmployeeMapper employeeMapper;

  @Override
  protected void addLinks(EntityModel<EmployeeDto> entityModel, Employee entity) {
    entityModel.add(
        linkTo(methodOn(EmployeeController.class).getEmployeeByUserId(entity.getUserId()))
            .withSelfRel());
  }

  @Override
  protected EmployeeDto fromEntityToDto(Employee entity) {
    return employeeMapper.fromEntityToDto(entity);
  }
}
