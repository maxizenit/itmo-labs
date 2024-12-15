package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnableToAssignUserToProjectException extends RuntimeException {

    /*
    static String  = "User with username \"\"";
    static String MESSAGE = "User with username \"%s\" is already assigned on project with id \"%s\"";

    public UnableToAssignUserToProjectException(String username, UUID projectId) {
        super(String.format(MESSAGE, username, projectId.toString()), HttpStatus.CONFLICT);
    }
     */
}
