package ru.itmo.credithistory.commons.controller;

import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpStatusCodeException;

@ControllerAdvice
@NullMarked
public class HttpStatusCodeExceptionHandler {

  @ExceptionHandler(HttpStatusCodeException.class)
  public ResponseEntity<?> handleHttpStatusCodeException(HttpStatusCodeException e) {
    return ResponseEntity.status(e.getStatusCode()).build();
  }
}
