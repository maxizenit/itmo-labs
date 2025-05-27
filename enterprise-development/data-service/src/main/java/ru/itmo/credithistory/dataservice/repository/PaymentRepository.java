package ru.itmo.credithistory.dataservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.itmo.credithistory.dataservice.entity.Payment;

public interface PaymentRepository
    extends CrudRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {}
