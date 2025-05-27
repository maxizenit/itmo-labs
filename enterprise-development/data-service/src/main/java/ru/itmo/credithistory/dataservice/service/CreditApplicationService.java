package ru.itmo.credithistory.dataservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.itmo.credithistory.dataservice.dto.filter.CreditApplicationFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditApplicationRqDto;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;

@NullMarked
public interface CreditApplicationService {

  @PostAuthorize("!hasRole('CUSTOMER') or returnObject.customerInn == #requesterCustomerInn")
  CreditApplication getCreditApplicationById(UUID id, @Nullable String requesterCustomerInn);

  @PreAuthorize("!hasRole('CUSTOMER') or #filter.customerInn == #requesterCustomerInn")
  List<CreditApplication> getCreditApplicationsByFilter(
      CreditApplicationFilterDto filter, @Nullable String requesterCustomerInn);

  List<CreditApplication> getAllCustomerCreditApplications(
      String customerInn, @Nullable LocalDateTime createdAtFrom);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  CreditApplication createCreditApplication(CreateOrUpdateCreditApplicationRqDto createDto);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  CreditApplication updateCreditApplicationById(
      UUID id, CreateOrUpdateCreditApplicationRqDto updateDto);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  void deleteCreditApplicationById(UUID id);
}
