package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class NoRightsForOperationException extends ForbiddenException {

    static String MESSAGE = "No rights for operation";

    public NoRightsForOperationException() {
        super(MESSAGE);
    }
}
