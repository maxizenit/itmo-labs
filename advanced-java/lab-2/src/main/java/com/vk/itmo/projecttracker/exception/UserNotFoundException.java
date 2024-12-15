package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UserNotFoundException extends NotFoundException {

    static String ID_MESSAGE = "User with id \"%s\" not found";
    static String USERNAME_MESSAGE = "User with username \"%s\" not found";

    public UserNotFoundException(UUID userId) {
        super(ID_MESSAGE.formatted(userId.toString()));
    }

    public UserNotFoundException(String username) {
        super(USERNAME_MESSAGE.formatted(username));
    }
}
