package com.vk.itmo.projecttracker.exception;

import org.springframework.http.HttpStatus;

public sealed class NotFoundException extends CommonException permits UserNotFoundException,
        ProjectNotFoundException,
        MilestoneNotFoundException,
        TicketNotFoundException,
        BugReportNotFoundException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
