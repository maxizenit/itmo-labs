package ru.itmo.credithistory.dataservice.service.impl;

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
import ru.itmo.credithistory.dataservice.dto.filter.CreditFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditRqDto;
import ru.itmo.credithistory.dataservice.entity.Credit;
import ru.itmo.credithistory.dataservice.exception.CreditNotFoundException;
import ru.itmo.credithistory.dataservice.mapper.CreditMapper;
import ru.itmo.credithistory.dataservice.repository.CreditRepository;
import ru.itmo.credithistory.dataservice.repository.specification.CreditFilterSpecificationBuilder;
import ru.itmo.credithistory.dataservice.service.CreditService;
import ru.itmo.credithistory.dataservice.util.CurrentUserIdProvider;

@Service
@RequiredArgsConstructor
@NullMarked
public class CreditServiceImpl implements CreditService {

  private final CreditRepository creditRepository;
  private final CreditMapper creditMapper;
  private final CreditFilterSpecificationBuilder creditFilterSpecificationBuilder;
  private final CurrentUserIdProvider currentUserIdProvider;

  @Override
  public Credit getCreditById(UUID id, @Nullable String requesterCustomerInn) {
    return creditRepository.findById(id).orElseThrow(CreditNotFoundException::new);
  }

  @Override
  public Credit getCreditByIdFetchPayments(UUID id, @Nullable String requesterCustomerInn) {
    return creditRepository.findByIdFetchPayments(id).orElseThrow(CreditNotFoundException::new);
  }

  @Override
  public Credit getCreditByIdFetchOverdue(UUID id, @Nullable String requesterCustomerInn) {
    return creditRepository.findByIdFetchOverdue(id).orElseThrow(CreditNotFoundException::new);
  }

  @Override
  public List<Credit> getCreditsByFilter(
      CreditFilterDto filter, @Nullable String requesterCustomerInn) {
    Specification<Credit> specification =
        creditFilterSpecificationBuilder.buildSpecificationFromFilter(filter);
    return creditRepository.findAll(specification);
  }

  @Override
  public List<Credit> getAllCustomerCreditsWithOverdue(String customerInn) {
    return creditRepository.findAllByCustomerInnWithOverdue(customerInn);
  }

  @Override
  public List<Credit> getAllCustomerCreditsWithPaymentsAndOverdue(String customerInn) {
    return creditRepository.findAllByCustomerInnWithPaymentsAndOverdue(customerInn);
  }

  @Transactional
  @Override
  public Credit createCredit(CreateOrUpdateCreditRqDto createDto) {
    Credit credit = creditMapper.fromCreateDtoToEntity(createDto);
    credit.setCreditOrganizationId(currentUserIdProvider.getCurrentUserId());
    return creditRepository.save(credit);
  }

  @Transactional
  @Override
  public Credit updateCreditById(UUID id, CreateOrUpdateCreditRqDto updateDto) {
    Credit credit = getCreditById(id, null);

    if (!Objects.equals(
        credit.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }

    creditMapper.updateEntityFromUpdateDto(credit, updateDto);
    return creditRepository.save(credit);
  }

  @Transactional
  @Override
  public void deleteCreditById(UUID id) {
    Credit credit = getCreditById(id, null);

    if (!Objects.equals(
        credit.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }

    creditRepository.deleteById(id);
  }
}
