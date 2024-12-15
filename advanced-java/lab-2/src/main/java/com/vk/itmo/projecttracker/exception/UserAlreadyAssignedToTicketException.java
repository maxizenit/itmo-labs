package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UserAlreadyAssignedToTicketException extends BadRequestException {

    static String MESSAGE = "User already assigned to ticket";

    public UserAlreadyAssignedToTicketException() {
        super(MESSAGE);
    }
}
