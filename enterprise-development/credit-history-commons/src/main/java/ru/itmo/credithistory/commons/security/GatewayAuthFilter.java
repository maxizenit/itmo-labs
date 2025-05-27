package ru.itmo.credithistory.commons.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@NullMarked
public class GatewayAuthFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String userId = request.getHeader(AuthorizationHeaders.USER_ID_HEADER);
    String role = request.getHeader(AuthorizationHeaders.USER_ROLE_HEADER);

    if (userId != null && role != null) {
      List<GrantedAuthority> authorities =
          List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

      Authentication auth =
          new UsernamePasswordAuthenticationToken(UUID.fromString(userId), null, authorities);
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }
}
