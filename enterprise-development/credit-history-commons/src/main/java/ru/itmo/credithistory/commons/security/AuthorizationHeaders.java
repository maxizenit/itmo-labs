package ru.itmo.credithistory.commons.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NullMarked;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@NullMarked
public class AuthorizationHeaders {

  public static final String USER_ID_HEADER = "X-User-Id";
  public static final String USER_ROLE_HEADER = "X-User-Role";
  public static final String CUSTOMER_INN_HEADER = "X-Customer-Inn";
}
