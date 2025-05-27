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
import ru.itmo.credithistory.dataservice.dto.filter.OverdueFilterDto;
import ru.itmo.credithistory.dataservice.entity.Overdue;

@Component
@NullMarked
public class OverdueFilterSpecificationBuilder
    extends FilterSpecificationBuilder<Overdue, OverdueFilterDto> {

  @Override
  protected void addPredicates(
      List<Predicate> predicates,
      OverdueFilterDto filter,
      Root<Overdue> root,
      @Nullable CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder) {
    if (filter.getCustomerInn() != null) {
      predicates.add(
          criteriaBuilder.equal(root.join("credit").get("customerInn"), filter.getCustomerInn()));
    }

    if (filter.getMinAmount() != null) {
      predicates.add(
          criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), filter.getMinAmount()));
    }

    if (filter.getRepaidAtFrom() != null) {
      predicates.add(
          criteriaBuilder.greaterThanOrEqualTo(root.get("repaidAt"), filter.getRepaidAtFrom()));
    }
  }
}
