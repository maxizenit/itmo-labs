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
import ru.itmo.credithistory.dataservice.dto.filter.PaymentFilterDto;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdatePaymentRqDto;
import ru.itmo.credithistory.dataservice.entity.Credit;
import ru.itmo.credithistory.dataservice.entity.Payment;
import ru.itmo.credithistory.dataservice.exception.PaymentNotFoundException;
import ru.itmo.credithistory.dataservice.mapper.PaymentMapper;
import ru.itmo.credithistory.dataservice.repository.PaymentRepository;
import ru.itmo.credithistory.dataservice.repository.specification.PaymentFilterSpecificationBuilder;
import ru.itmo.credithistory.dataservice.service.PaymentService;
import ru.itmo.credithistory.dataservice.util.CurrentUserIdProvider;

@Service
@RequiredArgsConstructor
@NullMarked
public class PaymentServiceImpl implements PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;
  private final PaymentFilterSpecificationBuilder paymentFilterSpecificationBuilder;
  private final CurrentUserIdProvider currentUserIdProvider;

  @Override
  public Payment getPaymentById(UUID id, @Nullable String requesterCustomerInn) {
    return paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
  }

  @Override
  public List<Payment> getPaymentsByFilter(
      PaymentFilterDto filter, @Nullable String requesterCustomerInn) {
    Specification<Payment> specification =
        paymentFilterSpecificationBuilder.buildSpecificationFromFilter(filter);
    return paymentRepository.findAll(specification);
  }

  @Transactional
  @Override
  public Payment createPayment(Credit credit, CreateOrUpdatePaymentRqDto createDto) {
    if (!Objects.equals(
        credit.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }
    Payment payment = paymentMapper.fromCreateDtoToEntity(createDto);
    payment.setCredit(credit);
    return paymentRepository.save(payment);
  }

  @Transactional
  @Override
  public Payment updatePaymentById(UUID id, CreateOrUpdatePaymentRqDto updateDto) {
    Payment payment = getPaymentById(id, null);
    Credit credit = payment.getCredit();
    if (!Objects.equals(
        credit.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }
    paymentMapper.updateEntityFromUpdateDto(payment, updateDto);
    return paymentRepository.save(payment);
  }

  @Transactional
  @Override
  public void deletePaymentById(UUID id) {
    Payment payment = getPaymentById(id, null);
    Credit credit = payment.getCredit();

    if (!Objects.equals(
        credit.getCreditOrganizationId(), currentUserIdProvider.getCurrentUserId())) {
      throw new ForbiddenException();
    }

    paymentRepository.deleteById(id);
  }
}
