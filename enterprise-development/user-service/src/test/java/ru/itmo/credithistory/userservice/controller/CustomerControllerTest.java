package ru.itmo.credithistory.userservice.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import ru.itmo.credithistory.userservice.dto.rq.CreateCustomerRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCustomerRqDto;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.Customer;
import ru.itmo.credithistory.userservice.repository.CustomerRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EmbeddedKafka
@Transactional
@NullMarked
class CustomerControllerTest {

  private final UUID requesterId = UUID.randomUUID();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TestUtils testUtils;
  @Autowired private CustomerRepository customerRepository;

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CUSTOMER")
  void testGetCustomerByUserId_success(UserRole requesterRole) throws Exception {
    Customer customer = testUtils.getTestCustomer();
    customerRepository.save(customer);

    mockMvc
        .perform(
            get("/customers/" + customer.getUserId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(customer.getUserId().toString()))
        .andExpect(jsonPath("$.inn").value("123456789012"))
        .andExpect(jsonPath("$.lastName").value("Клиентов"))
        .andExpect(jsonPath("$.firstName").value("Клиент"))
        .andExpect(jsonPath("$.middleName").value("Клиентович"))
        .andExpect(jsonPath("$.birthdate").value("2000-01-01"));
  }

  @Test
  void testGetCustomerByUserId_customerIsRequester_success() throws Exception {
    Customer customer = testUtils.getTestCustomer();
    customerRepository.save(customer);

    mockMvc
        .perform(
            get("/customers/" + customer.getUserId())
                .header(AuthorizationHeaders.USER_ID_HEADER, customer.getUserId())
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CUSTOMER))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(customer.getUserId().toString()))
        .andExpect(jsonPath("$.inn").value("123456789012"))
        .andExpect(jsonPath("$.lastName").value("Клиентов"))
        .andExpect(jsonPath("$.firstName").value("Клиент"))
        .andExpect(jsonPath("$.middleName").value("Клиентович"))
        .andExpect(jsonPath("$.birthdate").value("2000-01-01"));
  }

  @Test
  void testGetCustomerByUserId_forbidden() throws Exception {
    mockMvc
        .perform(
            get("/customers/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CUSTOMER))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CUSTOMER")
  void testGetCustomerByUserId_notFound(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/customers/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @Test
  void testGetCustomerByUserId_customerIsRequester_notFound() throws Exception {
    mockMvc
        .perform(
            get("/customers/" + requesterId)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CUSTOMER))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CUSTOMER")
  void testGetCustomerByInn_success(UserRole requesterRole) throws Exception {
    Customer customer = testUtils.getTestCustomer();
    customerRepository.save(customer);

    mockMvc
        .perform(
            get("/customers")
                .param("inn", "123456789012")
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(customer.getUserId().toString()))
        .andExpect(jsonPath("$.inn").value("123456789012"))
        .andExpect(jsonPath("$.lastName").value("Клиентов"))
        .andExpect(jsonPath("$.firstName").value("Клиент"))
        .andExpect(jsonPath("$.middleName").value("Клиентович"))
        .andExpect(jsonPath("$.birthdate").value("2000-01-01"));
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CUSTOMER")
  void testGetCustomerByInn_invalidInn(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/customers")
                .param("inn", "123")
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isBadRequest());
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CUSTOMER")
  void testGetCustomerByInn_notFound(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/customers")
                .param("inn", "123456789012")
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR", "EMPLOYEE"})
  void testCreateCustomer_success(UserRole requesterRole) throws Exception {
    CreateCustomerRqDto request =
        CreateCustomerRqDto.builder()
            .email(testUtils.getRandomEmail())
            .password("password")
            .inn("123456789012")
            .lastName("Клиентов")
            .firstName("Клиент")
            .middleName("Клиентович")
            .birthdate(LocalDate.of(2000, 1, 1))
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").isNotEmpty())
        .andExpect(jsonPath("$.inn").value("123456789012"))
        .andExpect(jsonPath("$.lastName").value("Клиентов"))
        .andExpect(jsonPath("$.firstName").value("Клиент"))
        .andExpect(jsonPath("$.middleName").value("Клиентович"))
        .andExpect(jsonPath("$.birthdate").value("2000-01-01"));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR", "EMPLOYEE"})
  void testCreateCustomer_forbidden(UserRole requesterRole) throws Exception {
    CreateCustomerRqDto request =
        CreateCustomerRqDto.builder()
            .email(testUtils.getRandomEmail())
            .password("password")
            .inn("123456789012")
            .lastName("Клиентов")
            .firstName("Клиент")
            .middleName("Клиентович")
            .birthdate(LocalDate.of(2000, 1, 1))
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR", "EMPLOYEE"})
  void testUpdateCustomerByUserId_success(UserRole requesterRole) throws Exception {
    Customer customer = testUtils.getTestCustomer();
    customerRepository.save(customer);

    UpdateCustomerRqDto request =
        UpdateCustomerRqDto.builder()
            .inn("012345678901")
            .lastName("Клиентова")
            .firstName("Клиентка")
            .middleName("Клиентовна")
            .birthdate(LocalDate.of(1990, 12, 12))
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/customers/" + customer.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(customer.getUserId().toString()))
        .andExpect(jsonPath("$.inn").value("012345678901"))
        .andExpect(jsonPath("$.lastName").value("Клиентова"))
        .andExpect(jsonPath("$.firstName").value("Клиентка"))
        .andExpect(jsonPath("$.middleName").value("Клиентовна"))
        .andExpect(jsonPath("$.birthdate").value("1990-12-12"));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR", "EMPLOYEE"})
  void testUpdateCustomerByUserId_forbidden(UserRole requesterRole) throws Exception {
    Customer customer = testUtils.getTestCustomer();
    customerRepository.save(customer);

    UpdateCustomerRqDto request =
        UpdateCustomerRqDto.builder()
            .inn("012345678901")
            .lastName("Клиентова")
            .firstName("Клиентка")
            .middleName("Клиентовна")
            .birthdate(LocalDate.of(1990, 12, 12))
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/customers/" + customer.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR", "EMPLOYEE"})
  void testUpdateCustomerByUserId_notFound(UserRole requesterRole) throws Exception {
    UpdateCustomerRqDto request =
        UpdateCustomerRqDto.builder()
            .inn("012345678901")
            .lastName("Клиентова")
            .firstName("Клиентка")
            .middleName("Клиентовна")
            .birthdate(LocalDate.of(1990, 12, 12))
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/customers/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR", "EMPLOYEE"})
  void testDeleteCustomerByUserId_success(UserRole requesterRole) throws Exception {
    Customer customer = testUtils.getTestCustomer();
    customerRepository.save(customer);
    UUID customerUserId = customer.getUserId();

    mockMvc
        .perform(
            delete("/customers/" + customerUserId)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNoContent());
    assertTrue(customerRepository.findById(customerUserId).isEmpty());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR", "EMPLOYEE"})
  void testDeleteCustomerByUserId_forbidden(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            delete("/customers/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR", "EMPLOYEE"})
  void testDeleteCustomerByUserId_notFound(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            delete("/customers/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }
}
