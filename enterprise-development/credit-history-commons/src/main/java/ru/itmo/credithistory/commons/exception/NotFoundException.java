package ru.itmo.credithistory.commons.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

public class NotFoundException extends HttpStatusCodeException {

  public NotFoundException() {
    super(HttpStatus.NOT_FOUND);
  }
}
