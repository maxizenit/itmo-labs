package com.vk.itmo.projecttracker.repository;

import com.vk.itmo.projecttracker.model.entity.Milestone;
import com.vk.itmo.projecttracker.model.entity.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface MilestoneRepository extends CrudRepository<Milestone, UUID> {

    boolean existsByProjectAndName(Project project, String name);
}
