package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MilestoneNotFoundException extends NotFoundException {

    static String MESSAGE = "Milestone with id \"%s\" not found";

    public MilestoneNotFoundException(String id) {
        super(MESSAGE.formatted(id));
    }
}
