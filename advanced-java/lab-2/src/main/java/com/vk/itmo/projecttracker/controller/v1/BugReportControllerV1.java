package com.vk.itmo.projecttracker.controller.v1;

import com.vk.itmo.projecttracker.exception.IncorrectStatusException;
import com.vk.itmo.projecttracker.model.dto.StatusDto;
import com.vk.itmo.projecttracker.model.enm.BugReportStatus;
import com.vk.itmo.projecttracker.service.BugReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/v1/bugreports")
public class BugReportControllerV1 {

    BugReportService bugReportService;

    @PutMapping("/{bugReportId}/status")
    public ResponseEntity<?> updateBugReportStatus(@PathVariable String bugReportId, @RequestBody StatusDto request) {
        var bugReport = bugReportService.getBugReportById(bugReportId);
        try {
            bugReportService.updateBugReportStatus(bugReport, BugReportStatus.valueOf(request.status()));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException _) {
            throw new IncorrectStatusException();
        }
    }
}
