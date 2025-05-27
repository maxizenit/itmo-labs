package ru.itmo.credithistory.userservice.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.commons.security.AuthorizationHeaders;
import ru.itmo.credithistory.userservice.TestUtils;
import ru.itmo.credithistory.userservice.dto.rq.LoginUserRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateUserEmailRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateUserPasswordRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateUserRoleRqDto;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EmbeddedKafka
@Transactional
@NullMarked
public class UserControllerTest {

  private final UUID requesterId = UUID.randomUUID();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TestUtils testUtils;
  @Autowired private UserRepository userRepository;

  @Test
  void testGetUserById_success() throws Exception {
    User user = testUtils.getTestUser();
    userRepository.save(user);

    mockMvc
        .perform(
            get("/users/" + user.getId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.ADMIN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.email").value(user.getEmail()))
        .andExpect(jsonPath("$.role").value(UserRole.CUSTOMER.name()));
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "ADMIN")
  void testGetUserById_nonAdminIsRequester_success(UserRole requesterRole) throws Exception {
    User user = testUtils.getTestUser();
    user.setRole(requesterRole);
    userRepository.save(user);

    mockMvc
        .perform(
            get("/users/" + user.getId())
                .header(AuthorizationHeaders.USER_ID_HEADER, user.getId())
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.email").value(user.getEmail()))
        .andExpect(jsonPath("$.role").value(user.getRole().name()));
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "ADMIN")
  void testGetUserById_forbidden(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/users/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @Test
  void testGetAllUsers_success() throws Exception {
    User user1 = testUtils.getTestUser();
    User user2 = testUtils.getTestUser();

    user2.setRole(UserRole.EMPLOYEE);

    userRepository.saveAll(List.of(user1, user2));

    mockMvc
        .perform(
            get("/users")
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.ADMIN))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$._embedded.userDtoList[*].email")
                .value(hasItems(user1.getEmail(), user2.getEmail())))
        .andExpect(
            jsonPath("$._embedded.userDtoList[*].role")
                .value(hasItems(UserRole.CUSTOMER.name(), UserRole.EMPLOYEE.name())));
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "ADMIN")
  void testGetAllUsers_forbidden(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/users")
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testUpdateUserEmailById_success(UserRole requesterRole) throws Exception {
    User user = testUtils.getTestUser();
    user.setRole(requesterRole);
    userRepository.save(user);

    String newEmail = testUtils.getRandomEmail();
    UpdateUserEmailRqDto request = UpdateUserEmailRqDto.builder().email(newEmail).build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + user.getId() + "/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, user.getId())
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(newEmail));
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testUpdateUserEmailById_forbidden(UserRole requesterRole) throws Exception {
    UpdateUserEmailRqDto request =
        UpdateUserEmailRqDto.builder().email(testUtils.getRandomEmail()).build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + UUID.randomUUID() + "/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testUpdateUserEmailById_notFound(UserRole requesterRole) throws Exception {
    UpdateUserEmailRqDto request =
        UpdateUserEmailRqDto.builder().email(testUtils.getRandomEmail()).build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + requesterId + "/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testUpdateUserPasswordById_success(UserRole requesterRole) throws Exception {
    User user = testUtils.getTestUser();
    user.setRole(requesterRole);
    userRepository.save(user);

    UpdateUserPasswordRqDto request =
        UpdateUserPasswordRqDto.builder().password("newpassword").build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + user.getId() + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, user.getId())
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNoContent());

    LoginUserRqDto loginRequest =
        LoginUserRqDto.builder().email(user.getEmail()).password("newpassword").build();
    String loginJson = objectMapper.writeValueAsString(loginRequest);

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
                .header(AuthorizationHeaders.USER_ID_HEADER, user.getId())
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testUpdateUserPasswordById_forbidden(UserRole requesterRole) throws Exception {
    UpdateUserPasswordRqDto request =
        UpdateUserPasswordRqDto.builder().password("newpassword").build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + UUID.randomUUID() + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testUpdateUserPasswordById_notFound(UserRole requesterRole) throws Exception {
    UpdateUserPasswordRqDto request =
        UpdateUserPasswordRqDto.builder().password("newpassword").build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + requesterId + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @MethodSource
  void testUpdateUserRoleById_success(Pair<UserRole, UserRole> oldAndNewRoles) throws Exception {
    User user = testUtils.getTestUser();
    user.setRole(oldAndNewRoles.getLeft());
    userRepository.save(user);

    UpdateUserRoleRqDto request =
        UpdateUserRoleRqDto.builder().role(oldAndNewRoles.getRight()).build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + user.getId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, user.getId())
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.ADMIN))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value(oldAndNewRoles.getRight().name()));
  }

  static Stream<Pair<UserRole, UserRole>> testUpdateUserRoleById_success() {
    return Stream.of(
        Pair.of(UserRole.EMPLOYEE, UserRole.SUPERVISOR),
        Pair.of(UserRole.SUPERVISOR, UserRole.EMPLOYEE),
        Pair.of(UserRole.CUSTOMER, UserRole.CUSTOMER));
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "ADMIN")
  void testUpdateUserRoleById_forbidden(UserRole requesterRole) throws Exception {
    UpdateUserRoleRqDto request = UpdateUserRoleRqDto.builder().role(UserRole.SUPERVISOR).build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + UUID.randomUUID() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource
  void testUpdateUserRoleById_incorrectRole_badRequest(Pair<UserRole, UserRole> oldAndNewRoles)
      throws Exception {
    User user = testUtils.getTestUser();
    user.setRole(oldAndNewRoles.getLeft());
    userRepository.save(user);

    UpdateUserRoleRqDto request =
        UpdateUserRoleRqDto.builder().role(oldAndNewRoles.getRight()).build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + user.getId() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, user.getId())
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.ADMIN))
        .andExpect(status().isBadRequest());
  }

  static Stream<Pair<UserRole, UserRole>> testUpdateUserRoleById_incorrectRole_badRequest() {
    return Stream.of(
        Pair.of(UserRole.ADMIN, UserRole.SUPERVISOR),
        Pair.of(UserRole.SUPERVISOR, UserRole.CUSTOMER),
        Pair.of(UserRole.CUSTOMER, UserRole.CREDIT_ORGANIZATION),
        Pair.of(UserRole.CREDIT_ORGANIZATION, UserRole.ADMIN),
        Pair.of(UserRole.EMPLOYEE, UserRole.ADMIN));
  }

  @Test
  void testUpdateUserRoleById_notFound() throws Exception {
    UpdateUserRoleRqDto request = UpdateUserRoleRqDto.builder().role(UserRole.SUPERVISOR).build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/users/" + UUID.randomUUID() + "/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.ADMIN))
        .andExpect(status().isNotFound());
  }
}
