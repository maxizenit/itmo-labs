package ru.itmo.credithistory.userservice.service.impl;

import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.commons.exception.BadRequestException;
import ru.itmo.credithistory.commons.kafka.KafkaTopics;
import ru.itmo.credithistory.commons.kafka.dto.UserRegistrationNotificationDto;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.exception.UserNotFoundException;
import ru.itmo.credithistory.userservice.repository.UserRepository;
import ru.itmo.credithistory.userservice.service.UserService;
import ru.itmo.credithistory.userservice.util.JwtTokenUtils;

@Service
@RequiredArgsConstructor
@NullMarked
public class UserServiceImpl implements UserService {

  private static final Map<UserRole, Set<UserRole>> ALLOWED_ROLE_UPDATES =
      Map.of(
          UserRole.EMPLOYEE, Set.of(UserRole.EMPLOYEE, UserRole.SUPERVISOR),
          UserRole.SUPERVISOR, Set.of(UserRole.EMPLOYEE, UserRole.SUPERVISOR));

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtils jwtTokenUtils;
  private final KafkaTemplate<String, UserRegistrationNotificationDto> kafkaTemplate;

  @Override
  public User getUserById(UUID id) {
    return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
  }

  @Override
  public User getUserByToken(String token) {
    UUID userId = jwtTokenUtils.getUserIdFromToken(token);
    return getUserById(userId);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  @Override
  public User createUser(String email, String rawPassword, UserRole role) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(rawPassword));
    user.setRole(role);
    userRepository.save(user);
    kafkaTemplate.send(
        KafkaTopics.USER_REGISTRATION_NOTIFICATION,
        UserRegistrationNotificationDto.builder().email(email).password(rawPassword).build());
    return user;
  }

  @Transactional
  @Override
  public User updateUserEmailById(UUID id, String email) {
    User user = getUserById(id);
    user.setEmail(email);
    return userRepository.save(user);
  }

  @Transactional
  @Override
  public User updateUserRoleById(UUID id, UserRole role) {
    User user = getUserById(id);
    if (isUpdateRoleAllowed(user.getRole(), role)) {
      user.setRole(role);
      return userRepository.save(user);
    } else {
      throw new BadRequestException();
    }
  }

  @Transactional
  @Override
  public void updateUserPasswordById(UUID id, String password) {
    User user = getUserById(id);
    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);
  }

  @Override
  public boolean adminRegistered() {
    return userRepository.findByRole(UserRole.ADMIN).isPresent();
  }

  private boolean isUpdateRoleAllowed(UserRole oldUserRole, UserRole newUserRole) {
    if (Objects.equals(oldUserRole, newUserRole)) {
      return true;
    }
    return ALLOWED_ROLE_UPDATES.getOrDefault(oldUserRole, Set.of()).contains(newUserRole);
  }
}
