package ru.itmo.credithistory.userservice.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.credithistory.userservice.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  Optional<Customer> findByInn(String inn);
}
