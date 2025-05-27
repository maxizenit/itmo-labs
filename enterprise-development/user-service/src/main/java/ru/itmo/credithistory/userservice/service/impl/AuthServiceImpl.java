package ru.itmo.credithistory.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmo.credithistory.commons.exception.UnauthorizedException;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.service.AuthService;
import ru.itmo.credithistory.userservice.service.UserService;
import ru.itmo.credithistory.userservice.util.JwtTokenUtils;

@Service
@RequiredArgsConstructor
@NullMarked
public class AuthServiceImpl implements AuthService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtils jwtTokenUtils;

  @Override
  public String generateToken(String email, String password) {
    User user = userService.getUserByEmail(email);
    String encodedPassword = user.getPassword();

    if (!passwordEncoder.matches(password, encodedPassword)) {
      throw new UnauthorizedException();
    }

    return jwtTokenUtils.generateToken(user.getId());
  }
}
