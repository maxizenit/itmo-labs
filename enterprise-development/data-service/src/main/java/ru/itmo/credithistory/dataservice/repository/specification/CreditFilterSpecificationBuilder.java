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
import ru.itmo.credithistory.dataservice.dto.filter.CreditFilterDto;
import ru.itmo.credithistory.dataservice.entity.Credit;

@Component
@NullMarked
public class CreditFilterSpecificationBuilder
    extends FilterSpecificationBuilder<Credit, CreditFilterDto> {

  @Override
  protected void addPredicates(
      List<Predicate> predicates,
      CreditFilterDto filter,
      Root<Credit> root,
      @Nullable CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    if (filter.getCustomerInn() != null) {
      predicates.add(criteriaBuilder.equal(root.get("customerInn"), filter.getCustomerInn()));
    }

    if (filter.getActive() != null) {
      predicates.add(criteriaBuilder.equal(root.get("active"), filter.getActive()));
    }
  }
}
