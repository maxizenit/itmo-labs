package com.vk.itmo.projecttracker.repository;

import com.vk.itmo.projecttracker.model.entity.UserProjectRole;
import org.springframework.data.repository.CrudRepository;

public interface UserProjectRoleRepository extends CrudRepository<UserProjectRole, UserProjectRole.UserProjectRoleId> {
}
