package com.vk.itmo.projecttracker.repository;

import com.vk.itmo.projecttracker.model.entity.BugReport;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface BugReportRepository extends CrudRepository<BugReport, UUID> {
}
