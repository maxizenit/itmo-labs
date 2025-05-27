package ru.itmo.credithistory.dataservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.itmo.credithistory.dataservice.entity.Overdue;

public interface OverdueRepository
    extends CrudRepository<Overdue, UUID>, JpaSpecificationExecutor<Overdue> {}
