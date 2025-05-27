package ru.itmo.credithistory.userservice.util;

import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.itmo.credithistory.userservice.enm.UserRole;

@Component
@NullMarked
public class CurrentUserRoleProvider {

  public UserRole getCurrentUserRole() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().parallelStream()
        .map(GrantedAuthority::getAuthority)
        .map(authority -> authority.replaceFirst("ROLE_", ""))
        .map(UserRole::valueOf)
        .findFirst()
        .orElseThrow();
  }
}
