package ru.itmo.credithistory.dataservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.credithistory.commons.security.AuthorizationHeaders;
import ru.itmo.credithistory.dataservice.dto.CreditDto;
import ru.itmo.credithistory.dataservice.dto.OverdueDto;
import ru.itmo.credithistory.dataservice.dto.PaymentDto;
import ru.itmo.credithistory.dataservice.dto.filter.CreditFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditRqDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateOverdueRqDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdatePaymentRqDto;
import ru.itmo.credithistory.dataservice.entity.Credit;
import ru.itmo.credithistory.dataservice.entity.Overdue;
import ru.itmo.credithistory.dataservice.entity.Payment;
import ru.itmo.credithistory.dataservice.hateoas.CreditAssembler;
import ru.itmo.credithistory.dataservice.hateoas.OverdueAssembler;
import ru.itmo.credithistory.dataservice.hateoas.PaymentAssembler;
import ru.itmo.credithistory.dataservice.service.CreditService;
import ru.itmo.credithistory.dataservice.service.OverdueService;
import ru.itmo.credithistory.dataservice.service.PaymentService;

@Tag(name = "Кредиты")
@Validated
@RestController
@RequestMapping("/credits")
@RequiredArgsConstructor
@NullMarked
public class CreditController {

  private final CreditService creditService;
  private final PaymentService paymentService;
  private final OverdueService overdueService;
  private final CreditAssembler creditAssembler;
  private final PaymentAssembler paymentAssembler;
  private final OverdueAssembler overdueAssembler;

  @Operation(
      summary = "Получение кредита",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор кредита",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Кредит"),
        @ApiResponse(responseCode = "404", description = "Кредит не найден")
      })
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<CreditDto>> getCreditById(
      @PathVariable UUID id,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    Credit credit = creditService.getCreditById(id, customerInnHeader);
    EntityModel<CreditDto> entityModel = creditAssembler.toModel(credit);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Получение платежей по кредиту",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Платежи по кредиту"),
        @ApiResponse(responseCode = "404", description = "Кредит не найден")
      })
  @GetMapping("/{id}/payments")
  public ResponseEntity<CollectionModel<EntityModel<PaymentDto>>> getPaymentsByCreditId(
      @PathVariable UUID id,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    Credit credit = creditService.getCreditByIdFetchPayments(id, customerInnHeader);
    Set<Payment> payments = credit.getPayments();
    CollectionModel<EntityModel<PaymentDto>> collectionModel =
        paymentAssembler.toCollectionModel(payments);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Получение просрочек по кредиту",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Просрочки по кредиту"),
        @ApiResponse(responseCode = "404", description = "Кредит не найден")
      })
  @GetMapping("/{id}/overdue")
  public ResponseEntity<CollectionModel<EntityModel<OverdueDto>>> getOverdueByCreditId(
      @PathVariable UUID id,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    Credit credit = creditService.getCreditByIdFetchOverdue(id, customerInnHeader);
    Set<Overdue> overdue = credit.getOverdue();
    CollectionModel<EntityModel<OverdueDto>> collectionModel =
        overdueAssembler.toCollectionModel(overdue);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Получение кредитов клиента",
      parameters = {
        @Parameter(
            in = ParameterIn.QUERY,
            name = "customerInn",
            description = "ИНН клиента",
            example = "123456789012",
            required = true),
        @Parameter(in = ParameterIn.QUERY, name = "active", description = "Активен ли кредит")
      },
      responses = @ApiResponse(responseCode = "200", description = "Кредиты клиента"))
  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<CreditDto>>> getCreditsByFilter(
      @RequestParam @NotBlank @Pattern(regexp = "^\\d{12}") String customerInn,
      @RequestParam(required = false) @Nullable Boolean active,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    CreditFilterDto filter =
        CreditFilterDto.builder().customerInn(customerInn).active(active).build();
    List<Credit> credits = creditService.getCreditsByFilter(filter, customerInnHeader);
    CollectionModel<EntityModel<CreditDto>> collectionModel =
        creditAssembler.toCollectionModel(credits);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Создание кредита",
      responses = @ApiResponse(responseCode = "201", description = "Кредит создан"))
  @PostMapping
  public ResponseEntity<EntityModel<CreditDto>> createCredit(
      @RequestBody @Valid CreateOrUpdateCreditRqDto createDto) {
    Credit credit = creditService.createCredit(createDto);
    EntityModel<CreditDto> entityModel = creditAssembler.toModel(credit);
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  @Operation(
      summary = "Создание платежа",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор кредита",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "201", description = "Платёж создан"),
        @ApiResponse(responseCode = "404", description = "Кредит не найден")
      })
  @PostMapping("/{id}/payments")
  public ResponseEntity<EntityModel<PaymentDto>> createPaymentForCreditById(
      @PathVariable UUID id, @RequestBody @Valid CreateOrUpdatePaymentRqDto createPaymentDto) {
    Credit credit = creditService.getCreditById(id, null);
    Payment payment = paymentService.createPayment(credit, createPaymentDto);
    EntityModel<PaymentDto> entityModel = paymentAssembler.toModel(payment);
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  @Operation(
      summary = "Создание просрочки",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор кредита",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "201", description = "Просрочка создана"),
        @ApiResponse(responseCode = "404", description = "Кредит не найден")
      })
  @PostMapping("/{id}/overdue")
  public ResponseEntity<EntityModel<OverdueDto>> createOverdueForCreditById(
      @PathVariable UUID id, @RequestBody @Valid CreateOrUpdateOverdueRqDto createOverdueDto) {
    Credit credit = creditService.getCreditById(id, null);
    Overdue overdue = overdueService.createOverdue(credit, createOverdueDto);
    EntityModel<OverdueDto> entityModel = overdueAssembler.toModel(overdue);
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  @Operation(
      summary = "Обновление кредита",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор кредита",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Кредит обновлён"),
        @ApiResponse(responseCode = "404", description = "Кредит не найден", content = @Content)
      })
  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<CreditDto>> updateCreditById(
      @PathVariable UUID id, @RequestBody @Valid CreateOrUpdateCreditRqDto updateDto) {
    Credit credit = creditService.updateCreditById(id, updateDto);
    EntityModel<CreditDto> entityModel = creditAssembler.toModel(credit);
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  @Operation(
      summary = "Удаление кредита",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор кредита",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "204", description = "Кредит удалён"),
        @ApiResponse(responseCode = "404", description = "Кредит не найден", content = @Content)
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCreditById(@PathVariable UUID id) {
    creditService.deleteCreditById(id);
    return ResponseEntity.noContent().build();
  }
}
