package ru.itmo.credithistory.userservice.controller;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import ru.itmo.credithistory.userservice.dto.rq.CreateEmployeeRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateEmployeeRqDto;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.Employee;
import ru.itmo.credithistory.userservice.repository.EmployeeRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EmbeddedKafka
@Transactional
@NullMarked
class EmployeeControllerTest {

  private final UUID requesterId = UUID.randomUUID();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TestUtils testUtils;
  @Autowired private EmployeeRepository employeeRepository;

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testGetEmployeeByUserId_success(UserRole requesterRole) throws Exception {
    Employee employee = testUtils.getTestEmployee();
    employeeRepository.save(employee);

    mockMvc
        .perform(
            get("/employees/" + employee.getUserId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastName").value("Сотрудников"))
        .andExpect(jsonPath("$.firstName").value("Сотрудник"))
        .andExpect(jsonPath("$.middleName").value("Сотрудникович"));
  }

  @Test
  void testGetEmployeeByUserId_employeeIsRequester_success() throws Exception {
    Employee employee = testUtils.getTestEmployee();
    employeeRepository.save(employee);

    mockMvc
        .perform(
            get("/employees/" + employee.getUserId())
                .header(AuthorizationHeaders.USER_ID_HEADER, employee.getUserId())
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.EMPLOYEE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastName").value("Сотрудников"))
        .andExpect(jsonPath("$.firstName").value("Сотрудник"))
        .andExpect(jsonPath("$.middleName").value("Сотрудникович"));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR"})
  void testGetEmployeeByUserId_forbidden(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/employees/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testGetEmployeeByUserId_notFound(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/employees/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetEmployeeByUserId_employeeIsRequester_notFound() throws Exception {
    mockMvc
        .perform(
            get("/employees/" + requesterId)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.EMPLOYEE))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testGetAllEmployees_success(UserRole requesterRole) throws Exception {
    Employee employee1 = testUtils.getTestEmployee();
    Employee employee2 = testUtils.getTestEmployee();

    employee2.setLastName("Сотрудникова");
    employee2.setFirstName("Сотрудница");
    employee2.setMiddleName("Сотрудниковна");

    employeeRepository.saveAll(List.of(employee1, employee2));

    mockMvc
        .perform(
            get("/employees")
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$._embedded.employeeDtoList[*].lastName")
                .value(hasItems("Сотрудников", "Сотрудникова")))
        .andExpect(
            jsonPath("$._embedded.employeeDtoList[*].firstName")
                .value(hasItems("Сотрудник", "Сотрудница")))
        .andExpect(
            jsonPath("$._embedded.employeeDtoList[*].middleName")
                .value(hasItems("Сотрудникович", "Сотрудниковна")));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR"})
  void testGetAllEmployees_forbidden(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/employees")
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testCreateEmployee_success(UserRole requesterRole) throws Exception {
    CreateEmployeeRqDto request =
        CreateEmployeeRqDto.builder()
            .email(testUtils.getRandomEmail())
            .password("password")
            .lastName("Сотрудников")
            .firstName("Сотрудник")
            .middleName("Сотрудникович")
            .role(UserRole.EMPLOYEE)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").isNotEmpty())
        .andExpect(jsonPath("$.lastName").value("Сотрудников"))
        .andExpect(jsonPath("$.firstName").value("Сотрудник"))
        .andExpect(jsonPath("$.middleName").value("Сотрудникович"));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testCreateEmployee_incorrectRole(UserRole requesterRole) throws Exception {
    CreateEmployeeRqDto request =
        CreateEmployeeRqDto.builder()
            .email(testUtils.getRandomEmail())
            .password("password")
            .lastName("Сотрудников")
            .firstName("Сотрудник")
            .middleName("Сотрудникович")
            .role(UserRole.CUSTOMER)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR"})
  void testCreateEmployee_forbidden(UserRole requesterRole) throws Exception {
    CreateEmployeeRqDto request =
        CreateEmployeeRqDto.builder()
            .email(testUtils.getRandomEmail())
            .password("password")
            .lastName("Сотрудников")
            .firstName("Сотрудник")
            .middleName("Сотрудникович")
            .role(UserRole.EMPLOYEE)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @Test
  void testCreateEmployee_supervisorCreatingSupervisor_forbidden() throws Exception {
    CreateEmployeeRqDto request =
        CreateEmployeeRqDto.builder()
            .email(testUtils.getRandomEmail())
            .password("password")
            .lastName("Сотрудников")
            .firstName("Сотрудник")
            .middleName("Сотрудникович")
            .role(UserRole.SUPERVISOR)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.SUPERVISOR))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testUpdateEmployeeByUserId_success(UserRole requesterRole) throws Exception {
    Employee employee = testUtils.getTestEmployee();
    employeeRepository.save(employee);

    UpdateEmployeeRqDto request =
        UpdateEmployeeRqDto.builder()
            .lastName("Сотрудникова")
            .firstName("Сотрудница")
            .middleName("Сотрудниковна")
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/employees/" + employee.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(employee.getUserId().toString()))
        .andExpect(jsonPath("$.lastName").value("Сотрудникова"))
        .andExpect(jsonPath("$.firstName").value("Сотрудница"))
        .andExpect(jsonPath("$.middleName").value("Сотрудниковна"));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR"})
  void testUpdateEmployeeByUserId_forbidden(UserRole requesterRole) throws Exception {
    UpdateEmployeeRqDto request =
        UpdateEmployeeRqDto.builder()
            .lastName("Сотрудникова")
            .firstName("Сотрудница")
            .middleName("Сотрудниковна")
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/employees/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @Test
  void testUpdateEmployeeByUserId_supervisorEditingSupervisor_forbidden() throws Exception {
    Employee employee = testUtils.getTestEmployee();
    employee.getUser().setRole(UserRole.SUPERVISOR);
    employeeRepository.save(employee);

    UpdateEmployeeRqDto request =
        UpdateEmployeeRqDto.builder()
            .lastName("Сотрудникова")
            .firstName("Сотрудница")
            .middleName("Сотрудниковна")
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/employees/" + employee.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.SUPERVISOR))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testUpdateEmployeeByUserId_notFound(UserRole requesterRole) throws Exception {
    UpdateEmployeeRqDto request =
        UpdateEmployeeRqDto.builder()
            .lastName("Сотрудникова")
            .firstName("Сотрудница")
            .middleName("Сотрудниковна")
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/employees/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testDeleteEmployee_success(UserRole requesterRole) throws Exception {
    Employee employee = testUtils.getTestEmployee();
    employeeRepository.save(employee);
    UUID employeeUserId = employee.getUserId();

    mockMvc
        .perform(
            delete("/employees/" + employeeUserId)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNoContent());
    assertTrue(employeeRepository.findById(employeeUserId).isEmpty());
  }

  @Test
  void testDeleteEmployee_supervisorDeletingSupervisor_forbidden() throws Exception {
    Employee employee = testUtils.getTestEmployee();
    employee.getUser().setRole(UserRole.SUPERVISOR);
    employeeRepository.save(employee);

    mockMvc
        .perform(
            delete("/employees/" + employee.getUserId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.SUPERVISOR))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testDeleteEmployee_notFound(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            delete("/employees/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }
}
