package com.vk.itmo.projecttracker.controller.v1;

import com.vk.itmo.projecttracker.exception.AddingNonExistentUserToProjectException;
import com.vk.itmo.projecttracker.exception.UserNotFoundException;
import com.vk.itmo.projecttracker.model.dto.BugReportDto;
import com.vk.itmo.projecttracker.model.dto.MilestoneDto;
import com.vk.itmo.projecttracker.model.dto.ProjectDto;
import com.vk.itmo.projecttracker.model.dto.UserIdDto;
import com.vk.itmo.projecttracker.model.dto.request.CreateBugReportRequestDto;
import com.vk.itmo.projecttracker.model.dto.request.CreateMilestoneRequestDto;
import com.vk.itmo.projecttracker.model.dto.request.CreateProjectRequestDto;
import com.vk.itmo.projecttracker.model.enm.ProjectRole;
import com.vk.itmo.projecttracker.model.mapper.BugReportMapper;
import com.vk.itmo.projecttracker.model.mapper.MilestoneMapper;
import com.vk.itmo.projecttracker.model.mapper.ProjectMapper;
import com.vk.itmo.projecttracker.service.BugReportService;
import com.vk.itmo.projecttracker.service.MilestoneService;
import com.vk.itmo.projecttracker.service.ProjectService;
import com.vk.itmo.projecttracker.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/v1/projects")
public class ProjectControllerV1 {

    ProjectService projectService;
    MilestoneService milestoneService;
    BugReportService bugReportService;
    UserService userService;
    ProjectMapper projectMapper;
    MilestoneMapper milestoneMapper;
    BugReportMapper bugReportMapper;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectRequestDto request) {
        var project = projectService.createProject(request.projectName(), request.description());
        var response = projectMapper.fromProjectToProjectDto(project);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{projectId}/teamleader")
    public ResponseEntity<?> assignTeamLeaderOnProject(@PathVariable String projectId, @RequestBody UserIdDto request) {
        var project = projectService.getProjectById(projectId);
        try {
            var teamleader = userService.getUserByUsername(request.username());
            projectService.addUserToProject(project, teamleader, ProjectRole.teamleader);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException _) {
            throw new AddingNonExistentUserToProjectException();
        }
    }

    @PostMapping("/{projectId}/developers")
    public ResponseEntity<?> addDeveloperToProject(@PathVariable String projectId, @RequestBody UserIdDto request) {
        var project = projectService.getProjectById(projectId);
        try {
            var developer = userService.getUserByUsername(request.username());
            projectService.addUserToProject(project, developer, ProjectRole.developer);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException _) {
            throw new AddingNonExistentUserToProjectException();
        }
    }

    @PostMapping("/{projectId}/testers")
    public ResponseEntity<?> addTesterToProject(@PathVariable String projectId, @RequestBody UserIdDto request) {
        var project = projectService.getProjectById(projectId);
        try {
            var tester = userService.getUserByUsername(request.username());
            projectService.addUserToProject(project, tester, ProjectRole.tester);
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException _) {
            throw new AddingNonExistentUserToProjectException();
        }
    }

    @PostMapping("/{projectId}/milestones")
    public ResponseEntity<MilestoneDto> createMilestone(@PathVariable String projectId,
                                                        @RequestBody CreateMilestoneRequestDto request) {
        var project = projectService.getProjectById(projectId);
        var milestone =
                milestoneService.createMilestone(project, request.name(), request.startDate(), request.endDate());
        var response = milestoneMapper.fromMilestoneToMilestoneDto(milestone);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{projectId}/bugreports")
    public ResponseEntity<BugReportDto> createBugReport(@PathVariable String projectId,
                                                        @RequestBody CreateBugReportRequestDto request) {
        var project = projectService.getProjectById(projectId);
        var bugReport = bugReportService.createBugReport(project, request.description());
        var response = bugReportMapper.fromBugReportToBugReportDto(bugReport);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{projectId}/test")
    public ResponseEntity<?> testProject(@PathVariable String projectId) {
        var project = projectService.getProjectById(projectId);
        projectService.testProject(project);
        return ResponseEntity.ok().build();
    }
}
