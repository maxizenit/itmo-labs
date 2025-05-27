package ru.itmo.credithistory.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class ForbiddenException extends HttpStatusCodeException {

  public ForbiddenException() {
    super(HttpStatus.FORBIDDEN);
  }
}
