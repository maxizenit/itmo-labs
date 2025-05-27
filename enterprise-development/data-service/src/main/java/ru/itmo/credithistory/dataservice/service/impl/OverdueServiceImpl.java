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
import ru.itmo.credithistory.dataservice.dto.filter.OverdueFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateOverdueRqDto;
import ru.itmo.credithistory.dataservice.entity.Credit;
import ru.itmo.credithistory.dataservice.entity.Overdue;
import ru.itmo.credithistory.dataservice.exception.OverdueNotFoundException;
import ru.itmo.credithistory.dataservice.mapper.OverdueMapper;
import ru.itmo.credithistory.dataservice.repository.OverdueRepository;
import ru.itmo.credithistory.dataservice.repository.specification.OverdueFilterSpecificationBuilder;
import ru.itmo.credithistory.dataservice.service.OverdueService;
import ru.itmo.credithistory.dataservice.util.CurrentUserIdProvider;

@Service
@RequiredArgsConstructor
@NullMarked
public class OverdueServiceImpl implements OverdueService {

  private final OverdueRepository overdueRepository;
  private final OverdueMapper overdueMapper;
  private final OverdueFilterSpecificationBuilder overdueFilterSpecificationBuilder;
  private final CurrentUserIdProvider currentUserIdProvider;

  @Override
  public Overdue getOverdueById(UUID id, @Nullable String requesterCustomerInn) {
    return overdueRepository.findById(id).orElseThrow(OverdueNotFoundException::new);
  }

  @Override
  public List<Overdue> getOverdueByFilter(
      OverdueFilterDto filter, @Nullable String requesterCustomerInn) {
    Specification<Overdue> specification =
        overdueFilterSpecificationBuilder.buildSpecificationFromFilter(filter);
    return overdueRepository.findAll(specification);
  }

  @Transactional
  @Override
  public Overdue createOverdue(Credit credit, CreateOrUpdateOverdueRqDto createDto) {
    if (!Objects.equals(
        credit.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }
    Overdue overdue = overdueMapper.fromCreateDtoToEntity(createDto);
    overdue.setCredit(credit);
    return overdueRepository.save(overdue);
  }

  @Transactional
  @Override
  public Overdue updateOverdueById(UUID id, CreateOrUpdateOverdueRqDto updateDto) {
    Overdue overdue = getOverdueById(id, null);
    Credit credit = overdue.getCredit();
    if (!Objects.equals(
        credit.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }
    overdueMapper.updateEntityFromUpdateDto(overdue, updateDto);
    return overdueRepository.save(overdue);
  }

  @Transactional
  @Override
  public void deleteOverdueById(UUID id) {
    Overdue overdue = getOverdueById(id, null);
    Credit credit = overdue.getCredit();

    if (!Objects.equals(
        credit.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }

    overdueRepository.deleteById(id);
  }
}
