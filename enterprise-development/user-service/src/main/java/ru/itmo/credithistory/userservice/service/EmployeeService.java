package ru.itmo.credithistory.userservice.service;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.itmo.credithistory.userservice.dto.rq.CreateEmployeeRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateEmployeeRqDto;
import ru.itmo.credithistory.userservice.entity.Employee;

@NullMarked
public interface EmployeeService {

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR') or #userId == authentication.principal")
  Employee getEmployeeByUserId(UUID userId);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
  List<Employee> getAllEmployees();

  @PreAuthorize(
      "hasRole('ADMIN') or (hasRole('SUPERVISOR') and #createDto.role.name() != 'SUPERVISOR')")
  Employee createEmployee(CreateEmployeeRqDto createDto);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
  Employee updateEmployeeByUserId(UUID userId, UpdateEmployeeRqDto updateDto);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
  void deleteEmployeeByUserId(UUID userId);
}
