package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UsernameAlreadyRegisteredException extends BadRequestException {

    static String MESSAGE = "Username \"%s\" already registered";

    public UsernameAlreadyRegisteredException(String username) {
        super(MESSAGE.formatted(username));
    }
}
