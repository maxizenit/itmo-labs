package ru.itmo.credithistory.dataservice.service;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.itmo.credithistory.dataservice.dto.filter.OverdueFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateOverdueRqDto;
import ru.itmo.credithistory.dataservice.entity.Credit;
import ru.itmo.credithistory.dataservice.entity.Overdue;

@NullMarked
public interface OverdueService {

  @PostAuthorize("!hasRole('CUSTOMER') or returnObject.credit.customerInn == #requesterCustomerInn")
  Overdue getOverdueById(UUID id, @Nullable String requesterCustomerInn);

  @PreAuthorize("!hasRole('CUSTOMER') or #filter.customerInn == #requesterCustomerInn")
  List<Overdue> getOverdueByFilter(OverdueFilterDto filter, @Nullable String requesterCustomerInn);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  Overdue createOverdue(Credit credit, CreateOrUpdateOverdueRqDto createDto);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  Overdue updateOverdueById(UUID id, CreateOrUpdateOverdueRqDto updateDto);

  @PreAuthorize("hasRole('CREDIT_ORGANIZATION')")
  void deleteOverdueById(UUID id);
}
