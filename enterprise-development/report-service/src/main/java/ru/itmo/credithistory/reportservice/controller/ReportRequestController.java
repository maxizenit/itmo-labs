package ru.itmo.credithistory.reportservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.credithistory.commons.security.AuthorizationHeaders;
import ru.itmo.credithistory.reportservice.dto.GetOrCreateReportRequestResultDto;
import ru.itmo.credithistory.reportservice.dto.ReportRequestDto;
import ru.itmo.credithistory.reportservice.hateaos.ReportRequestAssembler;
import ru.itmo.credithistory.reportservice.service.ReportRequestService;

@Tag(name = "Запросы отчётов о кредитной истории")
@Validated
@RestController
@RequestMapping("/report-requests")
@RequiredArgsConstructor
@NullMarked
public class ReportRequestController {

  private final ReportRequestService reportRequestService;
  private final ReportRequestAssembler reportRequestAssembler;

  @Operation(
      summary = "Получение актуального или создание нового запроса отчёта о кредитной истории",
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
            description = "Актуальный запрос отчёта о кредитной истории найден"),
        @ApiResponse(
            responseCode = "201",
            description = "Новый запрос отчёта о кредитной истории создан")
      })
  @PostMapping("/{customerInn}")
  public ResponseEntity<EntityModel<ReportRequestDto>> getOrCreateReportRequestByCustomerInn(
      @PathVariable @Pattern(regexp = "^\\d{12}") String customerInn,
      @RequestHeader(AuthorizationHeaders.CUSTOMER_INN_HEADER) String customerInnHeader) {
    GetOrCreateReportRequestResultDto getOrCreateReportRequestResultDto =
        reportRequestService.getOrCreateReportRequest(customerInn, customerInnHeader);
    HttpStatus httpStatus =
        getOrCreateReportRequestResultDto.isCreated() ? HttpStatus.CREATED : HttpStatus.OK;
    EntityModel<ReportRequestDto> entityModel =
        reportRequestAssembler.toModel(getOrCreateReportRequestResultDto.getReportRequest());
    return ResponseEntity.status(httpStatus).body(entityModel);
  }

  @Operation(
      summary = "Получение файла отчёта о кредитной истории",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "customerInn",
              description = "ИНН клиента",
              example = "123456789012",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Отчёт о кредитной истории в виде файла"),
        @ApiResponse(responseCode = "404", description = "Отчёт о кредитной истории ещё не готов")
      })
  @GetMapping("/{customerInn}/file")
  public ResponseEntity<ByteArrayResource> getReportByCustomerInn(
      @PathVariable @Pattern(regexp = "^\\d{12}") String customerInn,
      @RequestHeader(AuthorizationHeaders.CUSTOMER_INN_HEADER) String customerInnHeader) {
    byte[] bytes = reportRequestService.getReport(customerInn, customerInnHeader);
    ByteArrayResource resource = new ByteArrayResource(bytes);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"README.md\"")
        .contentLength(bytes.length)
        .contentType(MediaType.valueOf("text/markdown"))
        .body(resource);
  }
}
