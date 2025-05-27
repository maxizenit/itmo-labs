package ru.itmo.credithistory.commons.grpc;

import io.grpc.Status;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@NullMarked
public class GrpcStatusToHttpStatusMapper {

  public static HttpStatus mapGrpcToHttpStatus(Status status) {
    return switch (status.getCode()) {
      case NOT_FOUND -> HttpStatus.NOT_FOUND;
      case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
      case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
      case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
      case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
      case DEADLINE_EXCEEDED -> HttpStatus.GATEWAY_TIMEOUT;
      default -> HttpStatus.INTERNAL_SERVER_ERROR;
    };
  }
}
