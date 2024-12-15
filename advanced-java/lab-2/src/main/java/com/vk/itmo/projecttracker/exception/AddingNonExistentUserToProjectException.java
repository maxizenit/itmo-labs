package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class AddingNonExistentUserToProjectException extends BadRequestException {

    static String MESSAGE = "Adding non-existent user to project";

    public AddingNonExistentUserToProjectException() {
        super(MESSAGE);
    }
}
