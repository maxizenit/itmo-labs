package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ViewOtherUserProjectsException extends ForbiddenException {

    static String MESSAGE = "Viewing another user's projects is prohibited";

    public ViewOtherUserProjectsException() {
        super(MESSAGE);
    }
}
