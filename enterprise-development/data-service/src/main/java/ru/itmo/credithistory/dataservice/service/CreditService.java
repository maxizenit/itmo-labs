package ru.itmo.credithistory.dataservice.service;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.itmo.credithistory.dataservice.dto.filter.CreditFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditRqDto;
import ru.itmo.credithistory.dataservice.entity.Credit;

@NullMarked
public interface CreditService {

  @PostAuthorize("!hasRole('CUSTOMER') or returnObject.customerInn == #requesterCustomerInn")
  Credit getCreditById(UUID id, @Nullable String requesterCustomerInn);

  @PostAuthorize("!hasRole('CUSTOMER') or returnObject.customerInn == #requesterCustomerInn")
  Credit getCreditByIdFetchPayments(UUID id, @Nullable String requesterCustomerInn);

  @PostAuthorize("!hasRole('CUSTOMER') or returnObject.customerInn == #requesterCustomerInn")
  Credit getCreditByIdFetchOverdue(UUID id, @Nullable String requesterCustomerInn);

  @PreAuthorize("!hasRole('CUSTOMER') or #filter.customerInn == #requesterCustomerInn")
  List<Credit> getCreditsByFilter(CreditFilterDto filter, @Nullable String requesterCustomerInn);

  List<Credit> getAllCustomerCreditsWithOverdue(String customerInn);

  List<Credit> getAllCustomerCreditsWithPaymentsAndOverdue(String customerInn);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  Credit createCredit(CreateOrUpdateCreditRqDto createDto);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  Credit updateCreditById(UUID id, CreateOrUpdateCreditRqDto updateDto);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  void deleteCreditById(UUID id);
}
