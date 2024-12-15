package com.vk.itmo.projecttracker.service;

import com.vk.itmo.projecttracker.annotation.CheckProjectRoles;
import com.vk.itmo.projecttracker.exception.MilestoneNotFoundException;
import com.vk.itmo.projecttracker.exception.MilestoneOnThisProjectWithThisNameAlreadyExistsException;
import com.vk.itmo.projecttracker.model.enm.MilestoneStatus;
import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import com.vk.itmo.projecttracker.model.entity.Milestone;
import com.vk.itmo.projecttracker.model.entity.Project;
import com.vk.itmo.projecttracker.repository.MilestoneRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class MilestoneService {

    MilestoneRepository milestoneRepository;

    @Transactional
    @CheckProjectRoles(ProjectRole.manager)
    public synchronized Milestone createMilestone(@NonNull Project project,
                                                  @NonNull String name,
                                                  @NonNull LocalDate startDate,
                                                  @NonNull LocalDate endDate) {
        Milestone milestone = new Milestone();
        milestone.setProject(project);
        milestone.setName(name);
        milestone.setStartDate(startDate);
        milestone.setEndDate(endDate);
        milestone.setStatus(MilestoneStatus.open);

        if (milestoneRepository.existsByProjectAndName(project, name)) {
            throw new MilestoneOnThisProjectWithThisNameAlreadyExistsException();
        }
        return milestoneRepository.save(milestone);
    }

    @CheckProjectRoles(ProjectRole.manager)
    public void updateMilestoneStatus(@NonNull Milestone milestone, @NonNull MilestoneStatus status) {
        milestone.setStatus(status);
        milestoneRepository.save(milestone);
    }

    public Milestone getMilestoneById(@NonNull String milestoneId) {
        try {
            UUID uuid = UUID.fromString(milestoneId);
            return milestoneRepository.findById(uuid).orElseThrow(() -> new MilestoneNotFoundException(milestoneId));
        } catch (IllegalArgumentException _) {
            throw new MilestoneNotFoundException(milestoneId);
        }
    }
}
