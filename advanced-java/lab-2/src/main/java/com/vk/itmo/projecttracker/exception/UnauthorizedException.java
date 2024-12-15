package com.vk.itmo.projecttracker.exception;

import org.springframework.http.HttpStatus;

public sealed class UnauthorizedException extends CommonException permits InvalidLoginDetailsException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
