package com.vk.itmo.projecttracker.service;

import com.vk.itmo.projecttracker.annotation.CheckProjectRoles;
import com.vk.itmo.projecttracker.exception.NoRightsForOperationException;
import com.vk.itmo.projecttracker.exception.ProjectNotFoundException;
import com.vk.itmo.projecttracker.exception.ProjectWithThisNameAlreadyExistsException;
import com.vk.itmo.projecttracker.exception.UserAlreadyAssignedToRole;
import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import com.vk.itmo.projecttracker.model.entity.Project;
import com.vk.itmo.projecttracker.model.entity.User;
import com.vk.itmo.projecttracker.repository.ProjectRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class ProjectService {

    ProjectRepository projectRepository;
    UserService userService;

    @Transactional
    public synchronized Project createProject(@NonNull String projectName, @NonNull String description) {
        Project project = new Project();
        project.setName(projectName);
        project.setDescription(description);
        project.setUserProjectRoles(new HashMap<>());
        project.getUserProjectRoles().put(userService.getCurrentUser(), ProjectRole.manager);

        if (projectRepository.existsByName(projectName)) {
            throw new ProjectWithThisNameAlreadyExistsException(projectName);
        }
        return projectRepository.save(project);
    }

    public Project getProjectById(@NonNull String projectId) {
        try {
            UUID uuid = UUID.fromString(projectId);
            return projectRepository.findById(uuid).orElseThrow(() -> new ProjectNotFoundException(projectId));
        } catch (IllegalArgumentException _) {
            throw new ProjectNotFoundException(projectId);
        }
    }

    @Transactional
    @CheckProjectRoles(ProjectRole.manager)
    public synchronized void addUserToProject(@NonNull Project project, @NonNull User user, @NonNull ProjectRole role) {
        checkCurrentRole(project, user, role);

        switch (role) {
            case manager -> removeCurrentManager(project);
            case teamleader -> removeCurrentTeamleader(project);
        }
        removeUserFromProject(project, user);

        project.getUserProjectRoles().put(user, role);
        projectRepository.save(project);
    }

    @CheckProjectRoles(ProjectRole.tester)
    public void testProject(@NonNull Project project) {
        User currentUser = userService.getCurrentUser();
        ProjectRole currentUserRoleOnProject = project.getUserProjectRoles().get(currentUser);
        if (!ProjectRole.tester.equals(currentUserRoleOnProject)) {
            throw new NoRightsForOperationException();
        }
    }

    private void checkCurrentRole(@NonNull Project project, @NonNull User user, @NonNull ProjectRole role) {
        project.getUserProjectRoles()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(user))
                .filter(entry -> entry.getValue().equals(role))
                .findFirst()
                .ifPresent(_ -> {
                    throw new UserAlreadyAssignedToRole(role);
                });
    }

    private void removeCurrentManager(@NonNull Project project) {
        project.getUserProjectRoles()
                .entrySet()
                .stream()
                .filter(e -> ProjectRole.manager.equals(e.getValue()))
                .findFirst()
                .ifPresent(e -> project.getUserProjectRoles().remove(e.getKey()));
    }

    private void removeCurrentTeamleader(@NonNull Project project) {
        project.getUserProjectRoles()
                .entrySet()
                .stream()
                .filter(e -> ProjectRole.teamleader.equals(e.getValue()))
                .findFirst()
                .ifPresent(e -> project.getUserProjectRoles().remove(e.getKey()));
    }

    private void removeUserFromProject(@NonNull Project project, @NonNull User user) {
        project.getUserProjectRoles()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equals(user))
                .findFirst()
                .ifPresent(e -> project.getUserProjectRoles().remove(e.getKey()));
    }
}
