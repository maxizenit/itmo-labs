package ru.itmo.credithistory.dataservice.service;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.itmo.credithistory.dataservice.dto.filter.PaymentFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdatePaymentRqDto;
import ru.itmo.credithistory.dataservice.entity.Credit;
import ru.itmo.credithistory.dataservice.entity.Payment;

@NullMarked
public interface PaymentService {

  @PostAuthorize("!hasRole('CUSTOMER') or returnObject.credit.customerInn == #requesterCustomerInn")
  Payment getPaymentById(UUID id, @Nullable String requesterCustomerInn);

  @PreAuthorize("!hasRole('CUSTOMER') or #filter.customerInn == #requesterCustomerInn")
  List<Payment> getPaymentsByFilter(PaymentFilterDto filter, @Nullable String requesterCustomerInn);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  Payment createPayment(Credit credit, CreateOrUpdatePaymentRqDto createDto);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  Payment updatePaymentById(UUID id, CreateOrUpdatePaymentRqDto updateDto);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  void deletePaymentById(UUID id);
}
