package ru.itmo.credithistory.userservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;

public interface CreditOrganizationRepository extends JpaRepository<CreditOrganization, UUID> {}
