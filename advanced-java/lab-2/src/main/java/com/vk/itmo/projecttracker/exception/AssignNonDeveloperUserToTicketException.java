package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class AssignNonDeveloperUserToTicketException extends BadRequestException {

    static String MESSAGE = "Assign non developer user to ticket is prohibited";

    public AssignNonDeveloperUserToTicketException() {
        super(MESSAGE);
    }
}
