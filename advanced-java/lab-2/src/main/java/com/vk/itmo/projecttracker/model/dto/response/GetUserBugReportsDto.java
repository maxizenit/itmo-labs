package com.vk.itmo.projecttracker.model.dto.response;

public record GetUserBugReportsDto(String bugReportId, String description, String status) {
}
