package ru.itmo.credithistory.dataservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
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
import ru.itmo.credithistory.dataservice.dto.OverdueDto;
import ru.itmo.credithistory.dataservice.dto.filter.OverdueFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateOverdueRqDto;
import ru.itmo.credithistory.dataservice.entity.Overdue;
import ru.itmo.credithistory.dataservice.hateoas.OverdueAssembler;
import ru.itmo.credithistory.dataservice.service.OverdueService;

@Tag(name = "Просрочки")
@Validated
@RestController
@RequestMapping("/overdue")
@RequiredArgsConstructor
@NullMarked
public class OverdueController {

  private final OverdueService overdueService;
  private final OverdueAssembler overdueAssembler;

  @Operation(
      summary = "Получение просрочки",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор просрочки",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Просрочка"),
        @ApiResponse(responseCode = "404", description = "Просрочка не найдена")
      })
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<OverdueDto>> getOverdueById(
      @PathVariable UUID id,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    Overdue overdue = overdueService.getOverdueById(id, customerInnHeader);
    EntityModel<OverdueDto> entityModel = overdueAssembler.toModel(overdue);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Получение просрочек клиента",
      parameters = {
        @Parameter(
            in = ParameterIn.QUERY,
            name = "customerInn",
            description = "ИНН клиента",
            example = "123456789012",
            required = true),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "minAmount",
            description = "Минимальная сумма просрочки"),
        @Parameter(
            in = ParameterIn.QUERY,
            name = "repaidAtFrom",
            description = "Минимальное время погашения",
            example = "2025-01-01T10:30")
      },
      responses = @ApiResponse(responseCode = "200", description = "Просрочки клиента"))
  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<OverdueDto>>> getOverdueByFilter(
      @RequestParam @NotBlank @Pattern(regexp = "^\\d{12}") String customerInn,
      @RequestParam(required = false) @Nullable @DecimalMin("0.0") BigDecimal minAmount,
      @RequestParam(required = false) @Nullable @PastOrPresent LocalDateTime repaidAtFrom,
      @RequestHeader(value = AuthorizationHeaders.CUSTOMER_INN_HEADER, required = false)
          @Nullable String customerInnHeader) {
    OverdueFilterDto filter =
        OverdueFilterDto.builder()
            .customerInn(customerInn)
            .minAmount(minAmount)
            .repaidAtFrom(repaidAtFrom)
            .build();
    List<Overdue> overdue = overdueService.getOverdueByFilter(filter, customerInnHeader);
    CollectionModel<EntityModel<OverdueDto>> collectionModel =
        overdueAssembler.toCollectionModel(overdue);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Обновление просрочки",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор просрочки",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Просрочка обновлена"),
        @ApiResponse(responseCode = "404", description = "Просрочка не найдена", content = @Content)
      })
  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<OverdueDto>> updateOverdueById(
      @PathVariable UUID id, @RequestBody @Valid CreateOrUpdateOverdueRqDto updateDto) {
    Overdue overdue = overdueService.updateOverdueById(id, updateDto);
    EntityModel<OverdueDto> entityModel = overdueAssembler.toModel(overdue);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Удаление просрочки",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор просрочки",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "204", description = "Просрочка удалена"),
        @ApiResponse(responseCode = "404", description = "Просрочка не найдена", content = @Content)
      })
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteOverdueById(@PathVariable UUID id) {
    overdueService.deleteOverdueById(id);
    return ResponseEntity.noContent().build();
  }
}
