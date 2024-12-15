package com.vk.itmo.projecttracker.model.dto;

public record BugReportDto(String bugReportId, String projectId, String description, String status) {
}
