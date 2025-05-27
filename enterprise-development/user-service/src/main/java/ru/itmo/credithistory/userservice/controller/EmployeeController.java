package ru.itmo.credithistory.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.credithistory.userservice.dto.EmployeeDto;
import ru.itmo.credithistory.userservice.dto.rq.CreateEmployeeRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateEmployeeRqDto;
import ru.itmo.credithistory.userservice.entity.Employee;
import ru.itmo.credithistory.userservice.hateoas.EmployeeAssembler;
import ru.itmo.credithistory.userservice.service.EmployeeService;

@Tag(name = "Сотрудники")
@Validated
@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@NullMarked
public class EmployeeController {

  private final EmployeeService employeeService;
  private final EmployeeAssembler employeeAssembler;

  @Operation(
      summary = "Получение сотрудника",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Сотрудник"),
        @ApiResponse(responseCode = "404", description = "Сотрудник не найден", content = @Content)
      })
  @GetMapping("/{userId}")
  public ResponseEntity<EntityModel<EmployeeDto>> getEmployeeByUserId(@PathVariable UUID userId) {
    Employee employee = employeeService.getEmployeeByUserId(userId);
    EntityModel<EmployeeDto> entityModel = employeeAssembler.toModel(employee);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Получение всех сотрудников",
      responses = @ApiResponse(responseCode = "200", description = "Сотрудники"))
  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<EmployeeDto>>> getAllEmployees() {
    List<Employee> employees = employeeService.getAllEmployees();
    CollectionModel<EntityModel<EmployeeDto>> collectionModel =
        employeeAssembler.toCollectionModel(employees);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Создание сотрудника",
      responses = @ApiResponse(responseCode = "201", description = "Сотрудник создан"))
  @PostMapping
  public ResponseEntity<EntityModel<EmployeeDto>> createEmployee(
      @RequestBody @Valid CreateEmployeeRqDto createDto) {
    Employee employee = employeeService.createEmployee(createDto);
    EntityModel<EmployeeDto> entityModel = employeeAssembler.toModel(employee);
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  @Operation(
      summary = "Обновление сотрудника",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Сотрудник обновлён"),
        @ApiResponse(responseCode = "404", description = "Сотрудник не найден", content = @Content)
      })
  @PutMapping("/{userId}")
  public ResponseEntity<EntityModel<EmployeeDto>> updateEmployeeByUserId(
      @PathVariable UUID userId, @RequestBody @Valid UpdateEmployeeRqDto updateDto) {
    Employee employee = employeeService.updateEmployeeByUserId(userId, updateDto);
    EntityModel<EmployeeDto> entityModel = employeeAssembler.toModel(employee);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Удаление сотрудника",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "204", description = "Сотрудник удалён"),
        @ApiResponse(responseCode = "404", description = "Сотрудник не найден", content = @Content)
      })
  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteEmployeeByUserId(@PathVariable UUID userId) {
    employeeService.deleteEmployeeByUserId(userId);
    return ResponseEntity.noContent().build();
  }
}
