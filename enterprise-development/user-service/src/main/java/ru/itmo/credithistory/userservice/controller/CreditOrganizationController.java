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
import ru.itmo.credithistory.userservice.dto.CreditOrganizationDto;
import ru.itmo.credithistory.userservice.dto.rq.CreateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;
import ru.itmo.credithistory.userservice.hateoas.CreditOrganizationAssembler;
import ru.itmo.credithistory.userservice.service.CreditOrganizationService;

@Tag(name = "Кредитные организации")
@Validated
@RestController
@RequestMapping("/credit-organizations")
@RequiredArgsConstructor
@NullMarked
public class CreditOrganizationController {

  private final CreditOrganizationService creditOrganizationService;
  private final CreditOrganizationAssembler creditOrganizationAssembler;

  @Operation(
      summary = "Получение кредитной организации",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Кредитная организация"),
        @ApiResponse(
            responseCode = "404",
            description = "Кредитная организация не найдена",
            content = @Content)
      })
  @GetMapping("/{userId}")
  public ResponseEntity<EntityModel<CreditOrganizationDto>> getCreditOrganizationByUserId(
      @PathVariable UUID userId) {
    CreditOrganization creditOrganization =
        creditOrganizationService.getCreditOrganizationByUserId(userId);
    EntityModel<CreditOrganizationDto> entityModel =
        creditOrganizationAssembler.toModel(creditOrganization);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Получение всех кредитных организаций",
      responses = @ApiResponse(responseCode = "200", description = "Кредитные организации"))
  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<CreditOrganizationDto>>>
      getAllCreditOrganizations() {
    List<CreditOrganization> creditOrganizations =
        creditOrganizationService.getAllCreditOrganizations();
    CollectionModel<EntityModel<CreditOrganizationDto>> collectionModel =
        creditOrganizationAssembler.toCollectionModel(creditOrganizations);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Создание кредитной организации",
      responses = @ApiResponse(responseCode = "201", description = "Кредитная организация создана"))
  @PostMapping
  public ResponseEntity<EntityModel<CreditOrganizationDto>> createCreditOrganization(
      @RequestBody @Valid CreateCreditOrganizationRqDto createDto) {
    CreditOrganization creditOrganization =
        creditOrganizationService.createCreditOrganization(createDto);
    EntityModel<CreditOrganizationDto> entityModel =
        creditOrganizationAssembler.toModel(creditOrganization);
    return ResponseEntity.status(HttpStatus.CREATED).body(entityModel);
  }

  @Operation(
      summary = "Обновление кредитной организации",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Кредитная организация обновлена"),
        @ApiResponse(
            responseCode = "404",
            description = "Кредитная организация не найдена",
            content = @Content)
      })
  @PutMapping("/{userId}")
  public ResponseEntity<EntityModel<CreditOrganizationDto>> updateCreditOrganizationByUserId(
      @PathVariable UUID userId, @RequestBody @Valid UpdateCreditOrganizationRqDto updateDto) {
    CreditOrganization creditOrganization =
        creditOrganizationService.updateCreditOrganizationByUserId(userId, updateDto);
    EntityModel<CreditOrganizationDto> entityModel =
        creditOrganizationAssembler.toModel(creditOrganization);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Удаление кредитной организации",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "userId",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "204", description = "Кредитная организация удалена"),
        @ApiResponse(
            responseCode = "404",
            description = "Кредитная организация не найдена",
            content = @Content)
      })
  @DeleteMapping("/{userId}")
  public ResponseEntity<EntityModel<CreditOrganizationDto>> deleteCreditOrganizationByUserId(
      @PathVariable UUID userId) {
    creditOrganizationService.deleteCreditOrganizationByUserId(userId);
    return ResponseEntity.noContent().build();
  }
}
