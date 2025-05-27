package ru.itmo.credithistory.dataservice.repository.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.commons.repository.FilterSpecificationBuilder;
import ru.itmo.credithistory.dataservice.dto.filter.PaymentFilterDto;
import ru.itmo.credithistory.dataservice.entity.Payment;

@Component
@NullMarked
public class PaymentFilterSpecificationBuilder
    extends FilterSpecificationBuilder<Payment, PaymentFilterDto> {

  @Override
  protected void addPredicates(
      List<Predicate> predicates,
      PaymentFilterDto filter,
      Root<Payment> root,
      @Nullable CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    if (filter.getCustomerInn() != null) {
      predicates.add(
          criteriaBuilder.equal(root.join("credit").get("customerInn"), filter.getCustomerInn()));
    }

    if (filter.getPaidAtFrom() != null) {
      predicates.add(
          criteriaBuilder.greaterThanOrEqualTo(root.join("paidAt"), filter.getPaidAtFrom()));
    }
  }
}
