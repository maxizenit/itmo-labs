package ru.itmo.credithistory.scoringservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.credithistory.commons.security.AuthorizationHeaders;
import ru.itmo.credithistory.scoringservice.dto.GetOrCreateScoringRequestResultDto;
import ru.itmo.credithistory.scoringservice.dto.ScoringRequestDto;
import ru.itmo.credithistory.scoringservice.hateoas.ScoringRequestAssembler;
import ru.itmo.credithistory.scoringservice.service.ScoringRequestService;

@Tag(name = "Запросы расчётов кредитного рейтинга")
@Validated
@RestController
@RequestMapping("/scoring-requests")
@RequiredArgsConstructor
@NullMarked
public class ScoringRequestController {

  private final ScoringRequestService scoringRequestService;
  private final ScoringRequestAssembler scoringRequestAssembler;

  @Operation(
      summary = "Получение актуального или создание нового запроса кредитного рейтинга",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "customerInn",
              description = "ИНН клиента",
              example = "123456789012",
              required = true),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Актуальный запрос кредитного рейтинга найден"),
        @ApiResponse(responseCode = "201", description = "Новый запрос кредитного рейтинга создан")
      })
  @PostMapping("/{customerInn}")
  public ResponseEntity<EntityModel<ScoringRequestDto>> getOrCreateScoringRequestByCustomerInn(
      @PathVariable @Pattern(regexp = "^\\d{12}") String customerInn,
      @RequestHeader(AuthorizationHeaders.CUSTOMER_INN_HEADER) String customerInnHeader) {
    GetOrCreateScoringRequestResultDto getOrCreateScoringRequestResultDto =
        scoringRequestService.getOrCreateScoringRequest(customerInn, customerInnHeader, null);
    HttpStatus httpStatus =
        getOrCreateScoringRequestResultDto.isCreated() ? HttpStatus.CREATED : HttpStatus.OK;
    EntityModel<ScoringRequestDto> entityModel =
        scoringRequestAssembler.toModel(getOrCreateScoringRequestResultDto.getScoringRequest());
    return ResponseEntity.status(httpStatus).body(entityModel);
  }
}
