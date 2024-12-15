package com.vk.itmo.projecttracker.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public sealed class CommonException extends RuntimeException permits BadRequestException,
        UnauthorizedException,
        ForbiddenException,
        NotFoundException {

    HttpStatus status;

    public CommonException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
