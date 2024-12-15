package com.vk.itmo.projecttracker.exception;

import org.springframework.http.HttpStatus;

public sealed class BadRequestException extends CommonException permits UsernameAlreadyRegisteredException,
        ProjectWithThisNameAlreadyExistsException,
        UserAlreadyAssignedToRole,
        AddingNonExistentUserToProjectException,
        MilestoneOnThisProjectWithThisNameAlreadyExistsException,
        IncorrectStatusException,
        TicketOnThisMilestoneWithThisTitleAlreadyExistsException,
        AssignNonDeveloperUserToTicketException,
        UserAlreadyAssignedToTicketException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
