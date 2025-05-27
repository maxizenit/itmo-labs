package ru.itmo.credithistory.dataservice.util;

import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@NullMarked
public class CurrentUserIdProvider {

  public UUID getCurrentUserId() {
    return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
