package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ViewOtherUserBugReportsException extends ForbiddenException {

    static String MESSAGE = "Viewing another user's bugreports is prohibited";

    public ViewOtherUserBugReportsException() {
        super(MESSAGE);
    }
}
