package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CreatingTicketInClosedMilestoneException extends ForbiddenException {

    static String MESSAGE = "Creating ticket in closed milestone";

    public CreatingTicketInClosedMilestoneException() {
        super(MESSAGE);
    }
}
