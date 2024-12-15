package com.vk.itmo.projecttracker.service;

import com.vk.itmo.projecttracker.annotation.CheckProjectRoles;
import com.vk.itmo.projecttracker.exception.BugReportNotFoundException;
import com.vk.itmo.projecttracker.model.enm.BugReportStatus;
import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import com.vk.itmo.projecttracker.model.entity.BugReport;
import com.vk.itmo.projecttracker.model.entity.Project;
import com.vk.itmo.projecttracker.repository.BugReportRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BugReportService {

    BugReportRepository bugReportRepository;
    UserService userService;

    @CheckProjectRoles({ProjectRole.developer, ProjectRole.tester})
    public BugReport createBugReport(@NonNull Project project, @NonNull String description) {
        BugReport bugReport = new BugReport();
        bugReport.setProject(project);
        bugReport.setDescription(description);
        bugReport.setStatus(BugReportStatus.open);
        bugReport.setUser(userService.getCurrentUser());
        return bugReportRepository.save(bugReport);
    }

    public BugReport getBugReportById(@NonNull String bugReportId) {
        try {
            UUID uuid = UUID.fromString(bugReportId);
            return bugReportRepository.findById(uuid).orElseThrow(() -> new BugReportNotFoundException(bugReportId));
        } catch (IllegalArgumentException _) {
            throw new BugReportNotFoundException(bugReportId);
        }
    }

    @CheckProjectRoles({ProjectRole.developer, ProjectRole.tester})
    public void updateBugReportStatus(@NonNull BugReport bugReport, @NonNull BugReportStatus status) {
        bugReport.setStatus(status);
        bugReportRepository.save(bugReport);
    }
}
