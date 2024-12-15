package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ProjectNotFoundException extends NotFoundException {

    static String MESSAGE = "Project with id \"%s\" not found";

    public ProjectNotFoundException(String projectId) {
        super(String.format(MESSAGE, projectId));
    }
}
