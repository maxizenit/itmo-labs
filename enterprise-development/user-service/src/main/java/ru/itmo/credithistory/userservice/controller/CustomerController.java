package ru.itmo.credithistory.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.credithistory.userservice.dto.CustomerDto;
import ru.itmo.credithistory.userservice.dto.rq.CreateCustomerRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCustomerRqDto;
import ru.itmo.credithistory.userservice.entity.Customer;
import ru.itmo.credithistory.userservice.hateoas.CustomerAssembler;
import ru.itmo.credithistory.userservice.service.CustomerService;

@Tag(name = "Клиенты")
@Validated
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@NullMarked
public class CustomerController {

  private final CustomerService customerService;
  private final CustomerAssembler customerAssembler;

  @Operation(
      summary = "Получение клиента",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Клиент"),
        @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content)
      })
  @GetMapping("/{userId}")
  public ResponseEntity<EntityModel<CustomerDto>> getCustomerByUserId(@PathVariable UUID userId) {
    Customer customer = customerService.getCustomerByUserId(userId);
    EntityModel<CustomerDto> entityModel = customerAssembler.toModel(customer);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Получение клиента по ИНН",
      responses = {
        @ApiResponse(responseCode = "200", description = "Клиент"),
        @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content)
      },
      parameters =
          @Parameter(
              in = ParameterIn.QUERY,
              name = "inn",
              description = "ИНН клиента",
              required = true,
              example = "123456789012"))
  @GetMapping
  public ResponseEntity<EntityModel<CustomerDto>> getCustomerByInn(
      @RequestParam @NotBlank @Pattern(regexp = "^\\d{12}") String inn) {
    Customer customer = customerService.getCustomerByInn(inn);
    EntityModel<CustomerDto> entityModel = customerAssembler.toModel(customer);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Создание клиента",
      responses = @ApiResponse(responseCode = "201", description = "Клиент создан"))
  @PostMapping
  public ResponseEntity<EntityModel<CustomerDto>> createCustomer(
      @RequestBody @Valid CreateCustomerRqDto createDto) {
    Customer customer = customerService.createCustomer(createDto);
    EntityModel<CustomerDto> entityModel = customerAssembler.toModel(customer);
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  @Operation(
      summary = "Обновление клиента",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Клиент обновлён"),
        @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content)
      })
  @PutMapping("/{userId}")
  public ResponseEntity<EntityModel<CustomerDto>> updateCustomerByUserId(
      @PathVariable UUID userId, @RequestBody @Valid UpdateCustomerRqDto updateDto) {
    Customer customer = customerService.updateCustomerByUserId(userId, updateDto);
    EntityModel<CustomerDto> entityModel = customerAssembler.toModel(customer);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Удаление клиента",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "204", description = "Клиент удалён"),
        @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content)
      })
  @DeleteMapping("/{userId}")
  public ResponseEntity<?> deleteCustomerByUserId(@PathVariable UUID userId) {
    customerService.deleteCustomerByUserId(userId);
    return ResponseEntity.noContent().build();
  }
}
