package ru.itmo.credithistory.dataservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.itmo.credithistory.dataservice.entity.Credit;

public interface CreditRepository
    extends CrudRepository<Credit, UUID>, JpaSpecificationExecutor<Credit> {

  @Query("SELECT c FROM Credit c JOIN FETCH c.payments WHERE c.id = :id")
  Optional<Credit> findByIdFetchPayments(UUID id);

  @Query("SELECT c FROM Credit c JOIN FETCH c.overdue WHERE c.id = :id")
  Optional<Credit> findByIdFetchOverdue(UUID id);

  @Query("SELECT c FROM Credit c JOIN FETCH c.overdue WHERE c.customerInn = :customerInn")
  List<Credit> findAllByCustomerInnWithOverdue(String customerInn);

  @Query(
      "SELECT c FROM Credit c JOIN FETCH c.payments JOIN FETCH c.overdue WHERE c.customerInn = :customerInn")
  List<Credit> findAllByCustomerInnWithPaymentsAndOverdue(String customerInn);
}
