package ru.itmo.credithistory.commons.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NullMarked
public abstract class FilterSpecificationBuilder<E, F> {

  public Specification<E> buildSpecificationFromFilter(F filter) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      addPredicates(predicates, filter, root, criteriaQuery, criteriaBuilder);
      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  protected abstract void addPredicates(
      List<Predicate> predicates,
      F filter,
      Root<E> root,
      @Nullable CriteriaQuery<?> criteriaQuery,
      CriteriaBuilder criteriaBuilder);
}
