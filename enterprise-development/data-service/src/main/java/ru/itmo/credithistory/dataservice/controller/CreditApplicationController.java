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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.credithistory.commons.security.AuthorizationHeaders;
import ru.itmo.credithistory.dataservice.dto.CreditApplicationDto;
import ru.itmo.credithistory.dataservice.dto.filter.CreditApplicationFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditApplicationRqDto;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;
import ru.itmo.credithistory.dataservice.hateoas.CreditApplicationAssembler;
import ru.itmo.credithistory.dataservice.service.CreditApplicationService;

@Tag(name = "Кредитные заявки")
@Validated
@RestController
@RequestMapping("/credit-applications")
@RequiredArgsConstructor
@NullMarked
public class CreditApplicationController {

  private final CreditApplicationService creditApplicationService;
  private final CreditApplicationAssembler creditApplicationAssembler;

  @Operation(
      summary = "Получение кредитной заявки",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор кредитной заявки",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Кредитная заявка"),
        @ApiResponse(responseCode = "404", description = "Кредитная заявка не найдена")
      })
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<CreditApplicationDto>> getCreditApplicationById(
      @PathVariable UUID id,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    CreditApplication creditApplication =
        creditApplicationService.getCreditApplicationById(id, customerInnHeader);
    EntityModel<CreditApplicationDto> entityModel =
        creditApplicationAssembler.toModel(creditApplication);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Получение кредитных заявок клиента",
      parameters = {
        @Parameter(
            in = ParameterIn.QUERY,
            name = "customerInn",
            description = "ИНН клиента",
            example = "123456789012",
            required = true),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "createdAtFrom",
            description = "Минимальное время создания заявки",
            example = "2025-01-01T10:30")
      },
      responses = @ApiResponse(responseCode = "200", description = "Кредитные заявки клиента"))
  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<CreditApplicationDto>>>
      getCreditApplicationsByFilter(
          @RequestParam @NotBlank @Pattern(regexp = "^\\d{12}") String customerInn,
          @RequestParam(required = false) @Nullable @PastOrPresent LocalDateTime createdAtFrom,
          @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
              @Nullable String customerInnHeader) {
    CreditApplicationFilterDto filter =
        CreditApplicationFilterDto.builder()
            .customerInn(customerInn)
            .createdAtFrom(createdAtFrom)
            .build();
    List<CreditApplication> creditApplications =
        creditApplicationService.getCreditApplicationsByFilter(filter, customerInnHeader);
    CollectionModel<EntityModel<CreditApplicationDto>> collectionModel =
        creditApplicationAssembler.toCollectionModel(creditApplications);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Создание кредитной заявки",
      responses = @ApiResponse(responseCode = "201", description = "Кредитная заявка создана"))
  @PostMapping
  public ResponseEntity<EntityModel<CreditApplicationDto>> createCreditApplication(
      @RequestBody @Valid CreateOrUpdateCreditApplicationRqDto createDto) {
    CreditApplication creditApplication =
        creditApplicationService.createCreditApplication(createDto);
    EntityModel<CreditApplicationDto> entityModel =
        creditApplicationAssembler.toModel(creditApplication);
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  @Operation(
      summary = "Обновление кредитной заявки",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор кредитной заявки",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Кредитная заявка обновлена"),
        @ApiResponse(
            responseCode = "404",
            description = "Кредитная заявка не найдена",
            content = @Content)
      })
  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<CreditApplicationDto>> updateCreditApplicationById(
      @PathVariable UUID id, @RequestBody @Valid CreateOrUpdateCreditApplicationRqDto updateDto) {
    CreditApplication creditApplication =
        creditApplicationService.updateCreditApplicationById(id, updateDto);
    EntityModel<CreditApplicationDto> entityModel =
        creditApplicationAssembler.toModel(creditApplication);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Удаление кредитной заявки",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор кредитной заявки",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "204", description = "Кредитная заявка удалена"),
        @ApiResponse(
            responseCode = "404",
            description = "Кредитная заявка не найдена",
            content = @Content)
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteCreditApplicationById(@PathVariable UUID id) {
    creditApplicationService.deleteCreditApplicationById(id);
    return ResponseEntity.noContent().build();
  }
}
