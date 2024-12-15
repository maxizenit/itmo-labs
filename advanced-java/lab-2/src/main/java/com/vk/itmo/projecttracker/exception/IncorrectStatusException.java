package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class IncorrectStatusException extends BadRequestException {

    static String MESSAGE = "Incorrect status";

    public IncorrectStatusException() {
        super(MESSAGE);
    }
}
