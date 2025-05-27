package ru.itmo.credithistory.userservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.credithistory.userservice.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {}
