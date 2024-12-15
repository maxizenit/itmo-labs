package com.vk.itmo.projecttracker.model.dto;

import java.time.LocalDate;

public record MilestoneDto(String milestoneId,
                           String projectId,
                           LocalDate startDate,
                           LocalDate endDate,
                           String status) {
}
