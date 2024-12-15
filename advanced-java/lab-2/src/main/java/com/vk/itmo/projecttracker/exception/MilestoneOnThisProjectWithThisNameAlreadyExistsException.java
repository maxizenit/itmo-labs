package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MilestoneOnThisProjectWithThisNameAlreadyExistsException extends BadRequestException {

    static String MESSAGE = "Milestone on this project with this name already exists";

    public MilestoneOnThisProjectWithThisNameAlreadyExistsException() {
        super(MESSAGE);
    }
}
