package com.vk.itmo.projecttracker.model.dto;

public record TicketDto(String ticketId, String milestoneId, String title, String description, String status) {
}
