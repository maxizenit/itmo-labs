package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TicketNotFoundException extends NotFoundException {

    static String MESSAGE = "Ticket with id \"%s\" not found";

    public TicketNotFoundException(String id) {
        super(MESSAGE.formatted(id));
    }
}
