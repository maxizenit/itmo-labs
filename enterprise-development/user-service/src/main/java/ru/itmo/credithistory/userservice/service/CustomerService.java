package ru.itmo.credithistory.userservice.service;

import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.itmo.credithistory.userservice.dto.rq.CreateCustomerRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCustomerRqDto;
import ru.itmo.credithistory.userservice.entity.Customer;

@NullMarked
public interface CustomerService {

  @PreAuthorize("!hasRole('CUSTOMER') or #userId == authentication.principal")
  Customer getCustomerByUserId(UUID userId);

  Customer getCustomerByUserIdInternal(UUID userId);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLOYEE', 'CREDIT_ORGANIZATION')")
  Customer getCustomerByInn(String inn);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLOYEE')")
  Customer createCustomer(CreateCustomerRqDto createDto);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLOYEE')")
  Customer updateCustomerByUserId(UUID userId, UpdateCustomerRqDto updateDto);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'EMPLOYEE')")
  void deleteCustomerByUserId(UUID userId);
}
