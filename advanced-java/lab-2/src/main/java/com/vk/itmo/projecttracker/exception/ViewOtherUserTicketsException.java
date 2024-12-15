package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ViewOtherUserTicketsException extends ForbiddenException {

    static String MESSAGE = "Viewing another user's tickets is prohibited";

    public ViewOtherUserTicketsException() {
        super(MESSAGE);
    }
}
