package ru.itmo.credithistory.userservice.service;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.access.prepost.PreAuthorize;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.User;

@NullMarked
public interface UserService {

  @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal")
  User getUserById(UUID id);

  User getUserByEmail(String email);

  User getUserByToken(String token);

  @PreAuthorize("hasRole('ADMIN')")
  List<User> getAllUsers();

  User createUser(String email, String rawPassword, UserRole role);

  @PreAuthorize("#id == authentication.principal")
  User updateUserEmailById(UUID id, String email);

  @PreAuthorize("hasRole('ADMIN')")
  User updateUserRoleById(UUID id, UserRole role);

  @PreAuthorize("#id == authentication.principal")
  void updateUserPasswordById(UUID id, String password);

  boolean adminRegistered();
}
