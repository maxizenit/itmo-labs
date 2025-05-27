package ru.itmo.credithistory.userservice.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.userservice.dto.rq.CreateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.exception.CreditOrganizationNotFoundException;
import ru.itmo.credithistory.userservice.mapper.CreditOrganizationMapper;
import ru.itmo.credithistory.userservice.repository.CreditOrganizationRepository;
import ru.itmo.credithistory.userservice.service.CreditOrganizationService;
import ru.itmo.credithistory.userservice.service.UserService;

@Service
@RequiredArgsConstructor
@NullMarked
public class CreditOrganizationServiceImpl implements CreditOrganizationService {

  private final CreditOrganizationRepository creditOrganizationRepository;
  private final UserService userService;
  private final CreditOrganizationMapper creditOrganizationMapper;

  @Override
  public CreditOrganization getCreditOrganizationByUserId(UUID userId) {
    return creditOrganizationRepository
        .findById(userId)
        .orElseThrow(CreditOrganizationNotFoundException::new);
  }

  @Override
  public List<CreditOrganization> getAllCreditOrganizations() {
    return creditOrganizationRepository.findAll();
  }

  @Override
  public List<CreditOrganization> getCreditOrganizationsByIds(List<UUID> ids) {
    return creditOrganizationRepository.findAllById(ids);
  }

  @Transactional
  @Override
  public CreditOrganization createCreditOrganization(CreateCreditOrganizationRqDto createDto) {
    User user =
        userService.createUser(
            createDto.getEmail(), createDto.getPassword(), UserRole.CREDIT_ORGANIZATION);
    CreditOrganization creditOrganization =
        creditOrganizationMapper.fromCreateDtoToEntity(createDto);
    creditOrganization.setUser(user);
    return creditOrganizationRepository.save(creditOrganization);
  }

  @Transactional
  @Override
  public CreditOrganization updateCreditOrganizationByUserId(
      UUID userId, UpdateCreditOrganizationRqDto updateDto) {
    CreditOrganization creditOrganization = getCreditOrganizationByUserId(userId);
    creditOrganizationMapper.updateEntityFromUpdateDto(creditOrganization, updateDto);
    return creditOrganizationRepository.save(creditOrganization);
  }

  @Transactional
  @Override
  public void deleteCreditOrganizationByUserId(UUID userId) {
    if (!creditOrganizationRepository.existsById(userId)) {
      throw new CreditOrganizationNotFoundException();
    }
    creditOrganizationRepository.deleteById(userId);
  }
}
