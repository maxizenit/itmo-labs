package com.vk.itmo.projecttracker.model.dto.request;

import java.time.LocalDate;

public record CreateMilestoneRequestDto(String name, LocalDate startDate, LocalDate endDate) {
}
