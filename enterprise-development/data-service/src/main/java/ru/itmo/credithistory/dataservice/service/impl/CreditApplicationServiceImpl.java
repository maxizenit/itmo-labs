package ru.itmo.credithistory.dataservice.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.commons.exception.ForbiddenException;
import ru.itmo.credithistory.dataservice.dto.filter.CreditApplicationFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditApplicationRqDto;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;
import ru.itmo.credithistory.dataservice.exception.CreditApplicationNotFoundException;
import ru.itmo.credithistory.dataservice.mapper.CreditApplicationMapper;
import ru.itmo.credithistory.dataservice.repository.CreditApplicationRepository;
import ru.itmo.credithistory.dataservice.repository.specification.CreditApplicationFilterSpecificationBuilder;
import ru.itmo.credithistory.dataservice.service.CreditApplicationService;
import ru.itmo.credithistory.dataservice.util.CurrentUserIdProvider;

@Service
@RequiredArgsConstructor
@NullMarked
public class CreditApplicationServiceImpl implements CreditApplicationService {

  private final CreditApplicationRepository creditApplicationRepository;
  private final CreditApplicationMapper creditApplicationMapper;
  private final CreditApplicationFilterSpecificationBuilder
      creditApplicationFilterSpecificationBuilder;
  private final CurrentUserIdProvider currentUserIdProvider;

  @Override
  public CreditApplication getCreditApplicationById(
      UUID id, @Nullable String requesterCustomerInn) {
    return creditApplicationRepository
        .findById(id)
        .orElseThrow(CreditApplicationNotFoundException::new);
  }

  @Override
  public List<CreditApplication> getCreditApplicationsByFilter(
      CreditApplicationFilterDto filter, @Nullable String requesterCustomerInn) {
    Specification<CreditApplication> specification =
        creditApplicationFilterSpecificationBuilder.buildSpecificationFromFilter(filter);
    return creditApplicationRepository.findAll(specification);
  }

  @Override
  public List<CreditApplication> getAllCustomerCreditApplications(
      String customerInn, @Nullable LocalDateTime createdAtFrom) {
    return getCreditApplicationsByFilter(
        CreditApplicationFilterDto.builder()
            .customerInn(customerInn)
            .createdAtFrom(createdAtFrom)
            .build(),
        null);
  }

  @Transactional
  @Override
  public CreditApplication createCreditApplication(CreateOrUpdateCreditApplicationRqDto createDto) {
    CreditApplication creditApplication = creditApplicationMapper.fromCreateDtoToEntity(createDto);
    creditApplication.setCreditOrganizationId(currentUserIdProvider.getCurrentUserId());
    return creditApplicationRepository.save(creditApplication);
  }

  @Transactional
  @Override
  public CreditApplication updateCreditApplicationById(
      UUID id, CreateOrUpdateCreditApplicationRqDto updateDto) {
    CreditApplication creditApplication = getCreditApplicationById(id, null);
    if (!Objects.equals(
        creditApplication.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }
    creditApplicationMapper.updateEntityFromUpdateDto(creditApplication, updateDto);
    return creditApplicationRepository.save(creditApplication);
  }

  @Transactional
  @Override
  public void deleteCreditApplicationById(UUID id) {
    CreditApplication creditApplication = getCreditApplicationById(id, null);
    if (!Objects.equals(
        creditApplication.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }
    creditApplicationRepository.deleteById(id);
  }
}
