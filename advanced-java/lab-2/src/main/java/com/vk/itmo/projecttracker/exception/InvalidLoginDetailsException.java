package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class InvalidLoginDetailsException extends UnauthorizedException {

    static String MESSAGE = "Invalid login details";

    public InvalidLoginDetailsException() {
        super(MESSAGE);
    }
}
