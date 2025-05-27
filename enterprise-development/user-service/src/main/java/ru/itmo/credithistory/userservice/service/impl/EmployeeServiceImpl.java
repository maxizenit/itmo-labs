package ru.itmo.credithistory.userservice.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.userservice.dto.rq.CreateEmployeeRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateEmployeeRqDto;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.Employee;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.exception.EmployeeNotFoundException;
import ru.itmo.credithistory.userservice.exception.NoEmployeeRoleException;
import ru.itmo.credithistory.userservice.exception.SupervisorCannotModifySupervisorException;
import ru.itmo.credithistory.userservice.mapper.EmployeeMapper;
import ru.itmo.credithistory.userservice.repository.EmployeeRepository;
import ru.itmo.credithistory.userservice.service.EmployeeService;
import ru.itmo.credithistory.userservice.service.UserService;
import ru.itmo.credithistory.userservice.util.CurrentUserRoleProvider;

@Service
@RequiredArgsConstructor
@NullMarked
public class EmployeeServiceImpl implements EmployeeService {

  private static final Set<UserRole> EMPLOYEE_ROLES =
      Set.of(UserRole.SUPERVISOR, UserRole.EMPLOYEE);

  private final EmployeeRepository employeeRepository;
  private final UserService userService;
  private final EmployeeMapper employeeMapper;
  private final CurrentUserRoleProvider currentUserRoleProvider;

  @Override
  public Employee getEmployeeByUserId(UUID userId) {
    return employeeRepository.findById(userId).orElseThrow(EmployeeNotFoundException::new);
  }

  @Override
  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  @Transactional
  @Override
  public Employee createEmployee(CreateEmployeeRqDto createDto) {
    if (!EMPLOYEE_ROLES.contains(createDto.getRole())) {
      throw new NoEmployeeRoleException();
    }

    User user =
        userService.createUser(createDto.getEmail(), createDto.getPassword(), createDto.getRole());
    Employee employee = employeeMapper.fromCreateDtoToEntity(createDto);
    employee.setUser(user);
    return employeeRepository.save(employee);
  }

  @Transactional
  @Override
  public Employee updateEmployeeByUserId(UUID userId, UpdateEmployeeRqDto updateDto) {
    Employee employee = getEmployeeByUserId(userId);

    if (isChangeForbidden(employee)) {
      throw new SupervisorCannotModifySupervisorException();
    }

    employeeMapper.updateEntityFromUpdateDto(employee, updateDto);
    return employeeRepository.save(employee);
  }

  @Transactional
  @Override
  public void deleteEmployeeByUserId(UUID userId) {
    Employee employee = getEmployeeByUserId(userId);
    if (isChangeForbidden(employee)) {
      throw new SupervisorCannotModifySupervisorException();
    }
    employeeRepository.deleteById(userId);
  }

  private boolean isChangeForbidden(Employee employee) {
    UserRole requesterRole = currentUserRoleProvider.getCurrentUserRole();
    return UserRole.SUPERVISOR.equals(requesterRole)
        && !UserRole.EMPLOYEE.equals(employee.getUser().getRole());
  }
}
