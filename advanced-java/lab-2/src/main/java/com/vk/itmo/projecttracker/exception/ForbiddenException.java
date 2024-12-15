package com.vk.itmo.projecttracker.exception;

import org.springframework.http.HttpStatus;

public sealed class ForbiddenException extends CommonException permits ViewOtherUserProjectsException,
        NoRightsForOperationException,
        CreatingTicketInClosedMilestoneException,
        ViewOtherUserTicketsException,
        ViewOtherUserBugReportsException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
