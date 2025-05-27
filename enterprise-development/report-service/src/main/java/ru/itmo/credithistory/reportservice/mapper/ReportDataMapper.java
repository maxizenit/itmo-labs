package ru.itmo.credithistory.reportservice.mapper;

import java.math.BigDecimal;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.credithistory.commons.mapper.BaseMapperConfig;
import ru.itmo.credithistory.dataservice.grpc.*;
import ru.itmo.credithistory.reportservice.dto.*;

@Mapper(config = BaseMapperConfig.class)
public abstract class ReportDataMapper {

  @Mapping(target = "creditRating", expression = "java(creditRating)")
  @Mapping(
      target = "credits",
      expression =
          "java(customerDataGrpc.getCreditsList().stream().map(c -> fromCreditGrpcToReportDto(c, creditOrganizationIdNameMap)).toList())")
  @Mapping(
      target = "creditApplications",
      expression =
          "java(customerDataGrpc.getCreditApplicationsList().stream().map(a -> fromCreditApplicationGrpcToReportDto(a, creditOrganizationIdNameMap)).toList())")
  public abstract ReportDataDto fromCustomerDataGrpcToReportDataDto(
      GetCustomerDataResponse customerDataGrpc,
      @Context Integer creditRating,
      @Context Map<String, String> creditOrganizationIdNameMap);

  @Mapping(
      target = "bankName",
      expression = "java(creditOrganizationIdNameMap.get(credit.getCreditOrganizationId()))")
  @Mapping(target = "remainingAmount", expression = "java(parseBigDecimal(credit.getRepaidAt()))")
  @Mapping(target = "payments", source = "paymentsList")
  @Mapping(target = "overdue", source = "overdueList")
  public abstract CreditReportDto fromCreditGrpcToReportDto(
      Credit credit, @Context Map<String, String> creditOrganizationIdNameMap);

  public abstract PaymentReportDto fromPaymentGrpcToReportDto(Payment payment);

  public abstract OverdueReportDto fromOverdueGrpcToReportDto(Overdue overdue);

  @Mapping(
      target = "bankName",
      expression =
          "java(creditOrganizationIdNameMap.get(creditApplication.getCreditOrganizationId()))")
  public abstract CreditApplicationReportDto fromCreditApplicationGrpcToReportDto(
      CreditApplication creditApplication,
      @Context Map<String, String> creditOrganizationIdNameMap);

  protected BigDecimal parseBigDecimal(String bigDecimalString) {
    return StringUtils.isBlank(bigDecimalString) ? null : new BigDecimal(bigDecimalString);
  }
}
