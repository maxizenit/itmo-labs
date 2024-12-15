package com.vk.itmo.projecttracker.advice;

import com.vk.itmo.projecttracker.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class DefaultAdvice {

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<?> handleException(CommonException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getMessage());
    }
}
