package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class BugReportNotFoundException extends NotFoundException {

    static String MESSAGE = "Bugreport with id \"%s\" not found";

    public BugReportNotFoundException(String id) {
        super(MESSAGE.formatted(id));
    }
}
