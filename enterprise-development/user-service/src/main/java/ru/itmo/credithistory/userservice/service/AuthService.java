package ru.itmo.credithistory.userservice.service;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AuthService {

  String generateToken(String email, String password);
}
