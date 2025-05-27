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
import ru.itmo.credithistory.dataservice.dto.filter.CreditApplicationFilterDto;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;

@Component
@NullMarked
public class CreditApplicationFilterSpecificationBuilder
    extends FilterSpecificationBuilder<CreditApplication, CreditApplicationFilterDto> {

  @Override
  protected void addPredicates(
      List<Predicate> predicates,
      CreditApplicationFilterDto filter,
      Root<CreditApplication> root,
      @Nullable CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    if (filter.getCustomerInn() != null) {
      predicates.add(criteriaBuilder.equal(root.get("customerInn"), filter.getCustomerInn()));
    }

    if (filter.getCreatedAtFrom() != null) {
      predicates.add(
          criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAtFrom()));
    }
  }
}
