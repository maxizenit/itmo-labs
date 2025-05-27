package ru.itmo.credithistory.dataservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.credithistory.commons.security.AuthorizationHeaders;
import ru.itmo.credithistory.dataservice.dto.PaymentDto;
import ru.itmo.credithistory.dataservice.dto.filter.PaymentFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdatePaymentRqDto;
import ru.itmo.credithistory.dataservice.entity.Payment;
import ru.itmo.credithistory.dataservice.hateoas.PaymentAssembler;
import ru.itmo.credithistory.dataservice.service.PaymentService;

@Tag(name = "Платежи")
@Validated
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@NullMarked
public class PaymentController {

  private final PaymentService paymentService;
  private final PaymentAssembler paymentAssembler;

  @Operation(
      summary = "Получение платежа",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор платежа",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Платёж"),
        @ApiResponse(responseCode = "404", description = "Платёж не найден")
      })
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<PaymentDto>> getPaymentById(
      @PathVariable UUID id,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    Payment payment = paymentService.getPaymentById(id, customerInnHeader);
    EntityModel<PaymentDto> entityModel = paymentAssembler.toModel(payment);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Получение платежей клиента",
      parameters = {
        @Parameter(
            in = ParameterIn.QUERY,
            name = "customerInn",
            description = "ИНН клиента",
            example = "123456789012",
            required = true),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "paidAtFrom",
            description = "Минимальное время платежа",
            example = "2025-01-01T10:30")
      },
      responses = @ApiResponse(responseCode = "200", description = "Платежи клиента"))
  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<PaymentDto>>> getPaymentsByFilter(
      @RequestParam @NotBlank @Pattern(regexp = "^\\d{12}") String customerInn,
      @RequestParam(required = false) @Nullable @PastOrPresent LocalDateTime paidAtFrom,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    PaymentFilterDto filter =
        PaymentFilterDto.builder().customerInn(customerInn).paidAtFrom(paidAtFrom).build();
    List<Payment> payments = paymentService.getPaymentsByFilter(filter, customerInnHeader);
    CollectionModel<EntityModel<PaymentDto>> collectionModel =
        paymentAssembler.toCollectionModel(payments);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Обновление платежа",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор платежа",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Платёж обновлён"),
        @ApiResponse(responseCode = "404", description = "Платёж не найден", content = @Content)
      })
  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<PaymentDto>> updatePaymentById(
      @PathVariable UUID id, @RequestBody @Valid CreateOrUpdatePaymentRqDto updateDto) {
    Payment payment = paymentService.updatePaymentById(id, updateDto);
    EntityModel<PaymentDto> entityModel = paymentAssembler.toModel(payment);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Удаление платежа",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор платежа",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "204", description = "Платёж удалён"),
        @ApiResponse(responseCode = "404", description = "Платёж не найден", content = @Content)
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePaymentById(@PathVariable UUID id) {
    paymentService.deletePaymentById(id);
    return ResponseEntity.noContent().build();
  }
}
