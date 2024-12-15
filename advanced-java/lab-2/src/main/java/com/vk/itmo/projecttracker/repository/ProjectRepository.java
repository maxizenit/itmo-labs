package com.vk.itmo.projecttracker.repository;

import com.vk.itmo.projecttracker.model.entity.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProjectRepository extends CrudRepository<Project, UUID> {

    boolean existsByName(String name);
}
