package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class TicketOnThisMilestoneWithThisTitleAlreadyExistsException extends BadRequestException {

    static String MESSAGE = "Ticket on this milestone with this title already exists";

    public TicketOnThisMilestoneWithThisTitleAlreadyExistsException() {
        super(MESSAGE);
    }
}
