package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ProjectWithThisNameAlreadyExistsException extends BadRequestException {

    static String MESSAGE = "Project with name \"%s\" already exists";

    public ProjectWithThisNameAlreadyExistsException(String projectName) {
        super(MESSAGE.formatted(projectName));
    }
}
