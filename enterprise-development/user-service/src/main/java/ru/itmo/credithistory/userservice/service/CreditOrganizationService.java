package ru.itmo.credithistory.userservice.service;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.itmo.credithistory.userservice.dto.rq.CreateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;

@NullMarked
public interface CreditOrganizationService {

  CreditOrganization getCreditOrganizationByUserId(UUID userId);

  List<CreditOrganization> getAllCreditOrganizations();

  List<CreditOrganization> getCreditOrganizationsByIds(List<UUID> ids);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
  CreditOrganization createCreditOrganization(CreateCreditOrganizationRqDto createDto);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
  CreditOrganization updateCreditOrganizationByUserId(
      UUID userId, UpdateCreditOrganizationRqDto updateDto);

  @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
  void deleteCreditOrganizationByUserId(UUID userId);
}
