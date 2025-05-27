package ru.itmo.credithistory.commons.grpc.controller;

import io.grpc.StatusException;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.itmo.credithistory.commons.grpc.GrpcStatusToHttpStatusMapper;

@ControllerAdvice
@NullMarked
public class GrpcStatusExceptionHandler {

  @ExceptionHandler(StatusException.class)
  public ResponseEntity<?> handleStatusException(StatusException e) {
    HttpStatus status = GrpcStatusToHttpStatusMapper.mapGrpcToHttpStatus(e.getStatus());
    return ResponseEntity.status(status.value()).build();
  }
}
