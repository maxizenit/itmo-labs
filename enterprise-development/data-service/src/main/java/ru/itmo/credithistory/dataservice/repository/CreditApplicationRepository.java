package ru.itmo.credithistory.dataservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;

public interface CreditApplicationRepository
    extends CrudRepository<CreditApplication, UUID>, JpaSpecificationExecutor<CreditApplication> {}
