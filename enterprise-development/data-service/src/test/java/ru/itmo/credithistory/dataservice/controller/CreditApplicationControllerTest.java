package ru.itmo.credithistory.dataservice.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.commons.security.AuthorizationHeaders;
import ru.itmo.credithistory.dataservice.TestUtils;
import ru.itmo.credithistory.dataservice.dto.rq.CreateOrUpdateCreditApplicationRqDto;
import ru.itmo.credithistory.dataservice.enm.CreditApplicationStatus;
import ru.itmo.credithistory.dataservice.enm.CreditType;
import ru.itmo.credithistory.dataservice.entity.CreditApplication;
import ru.itmo.credithistory.dataservice.repository.CreditApplicationRepository;
import ru.itmo.credithistory.userservice.enm.UserRole;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@NullMarked
class CreditApplicationControllerTest {

  private final UUID requesterId = UUID.randomUUID();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TestUtils testUtils;
  @Autowired private CreditApplicationRepository creditApplicationRepository;

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CUSTOMER")
  void testGetCreditApplicationById_success(UserRole requesterRole) throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplicationRepository.save(creditApplication);

    mockMvc
        .perform(
            get("/credit-applications/" + creditApplication.getId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(creditApplication.getId().toString()))
        .andExpect(jsonPath("$.customerInn").value("123456789012"))
        .andExpect(
            jsonPath("$.creditOrganizationId")
                .value(creditApplication.getCreditOrganizationId().toString()))
        .andExpect(jsonPath("$.creditType").value(CreditType.DEFAULT.name()))
        .andExpect(jsonPath("$.amount").value("25000"))
        .andExpect(jsonPath("$.createdAt").value("2025-01-01T10:30:00"))
        .andExpect(jsonPath("$.status").value(CreditApplicationStatus.CREATED.name()));
  }

  @Test
  void testGetCreditApplicationById_customerIsRequester_success() throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplicationRepository.save(creditApplication);

    mockMvc
        .perform(
            get("/credit-applications/" + creditApplication.getId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CUSTOMER)
                .header(AuthorizationHeaders.CUSTOMER_INN_HEADER, "123456789012"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(creditApplication.getId().toString()))
        .andExpect(jsonPath("$.customerInn").value("123456789012"))
        .andExpect(
            jsonPath("$.creditOrganizationId")
                .value(creditApplication.getCreditOrganizationId().toString()))
        .andExpect(jsonPath("$.creditType").value(CreditType.DEFAULT.name()))
        .andExpect(jsonPath("$.amount").value("25000"))
        .andExpect(jsonPath("$.createdAt").value("2025-01-01T10:30:00"))
        .andExpect(jsonPath("$.status").value(CreditApplicationStatus.CREATED.name()));
  }

  @Test
  void testGetCreditApplicationById_customerIsRequester_forbidden() throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplicationRepository.save(creditApplication);

    mockMvc
        .perform(
            get("/credit-applications/" + creditApplication.getId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CUSTOMER)
                .header(AuthorizationHeaders.CUSTOMER_INN_HEADER, "012345678901"))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class)
  void testGetCreditApplicationById_notFound(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/credit-applications/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @Test
  void testCreateCreditApplication_success() throws Exception {
    CreateOrUpdateCreditApplicationRqDto request =
        CreateOrUpdateCreditApplicationRqDto.builder()
            .customerInn("123456789012")
            .creditType(CreditType.CREDIT_CARD)
            .amount(BigDecimal.valueOf(30000))
            .createdAt(LocalDateTime.of(2025, 1, 1, 10, 30))
            .status(CreditApplicationStatus.APPROVED)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/credit-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CREDIT_ORGANIZATION))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.customerInn").value("123456789012"))
        .andExpect(jsonPath("$.creditOrganizationId").value(requesterId.toString()))
        .andExpect(jsonPath("$.creditType").value(CreditType.CREDIT_CARD.name()))
        .andExpect(jsonPath("$.amount").value("30000"))
        .andExpect(jsonPath("$.createdAt").value("2025-01-01T10:30:00"))
        .andExpect(jsonPath("$.status").value(CreditApplicationStatus.APPROVED.name()));
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CREDIT_ORGANIZATION")
  void testCreateCreditApplication_forbidden(UserRole requesterRole) throws Exception {
    CreateOrUpdateCreditApplicationRqDto request =
        CreateOrUpdateCreditApplicationRqDto.builder()
            .customerInn("123456789012")
            .creditType(CreditType.CREDIT_CARD)
            .amount(BigDecimal.valueOf(30000))
            .createdAt(LocalDateTime.of(2025, 1, 1, 10, 30))
            .status(CreditApplicationStatus.APPROVED)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/credit-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @Test
  void testUpdateCreditApplicationById_success() throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplication.setCreditOrganizationId(requesterId);
    creditApplicationRepository.save(creditApplication);

    CreateOrUpdateCreditApplicationRqDto request =
        CreateOrUpdateCreditApplicationRqDto.builder()
            .customerInn("123456789012")
            .creditType(CreditType.MORTGAGE)
            .amount(BigDecimal.valueOf(12000))
            .createdAt(LocalDateTime.of(2024, 2, 5, 12, 25, 33))
            .status(CreditApplicationStatus.REJECTED)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/credit-applications/" + creditApplication.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CREDIT_ORGANIZATION))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(creditApplication.getId().toString()))
        .andExpect(jsonPath("$.customerInn").value("123456789012"))
        .andExpect(jsonPath("$.creditOrganizationId").value(requesterId.toString()))
        .andExpect(jsonPath("$.creditType").value(CreditType.MORTGAGE.name()))
        .andExpect(jsonPath("$.amount").value("12000"))
        .andExpect(jsonPath("$.createdAt").value("2024-02-05T12:25:33"))
        .andExpect(jsonPath("$.status").value(CreditApplicationStatus.REJECTED.name()));
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CREDIT_ORGANIZATION")
  void testUpdateCreditApplicationById_forbidden(UserRole requesterRole) throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplication.setCreditOrganizationId(requesterId);
    creditApplicationRepository.save(creditApplication);

    CreateOrUpdateCreditApplicationRqDto request =
        CreateOrUpdateCreditApplicationRqDto.builder()
            .customerInn("123456789012")
            .creditType(CreditType.MORTGAGE)
            .amount(BigDecimal.valueOf(12000))
            .createdAt(LocalDateTime.of(2024, 2, 5, 12, 25, 33))
            .status(CreditApplicationStatus.REJECTED)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/credit-applications/" + creditApplication.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @Test
  void testUpdateCreditApplicationById_creditOrganizationEditsForeignCreditApplication_forbidden()
      throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplicationRepository.save(creditApplication);

    CreateOrUpdateCreditApplicationRqDto request =
        CreateOrUpdateCreditApplicationRqDto.builder()
            .customerInn("123456789012")
            .creditType(CreditType.MORTGAGE)
            .amount(BigDecimal.valueOf(12000))
            .createdAt(LocalDateTime.of(2024, 2, 5, 12, 25, 33))
            .status(CreditApplicationStatus.REJECTED)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/credit-applications/" + creditApplication.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CREDIT_ORGANIZATION))
        .andExpect(status().isForbidden());
  }

  @Test
  void testDeleteCreditApplicationById_success() throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplication.setCreditOrganizationId(requesterId);
    creditApplicationRepository.save(creditApplication);
    UUID creditApplicationId = creditApplication.getId();

    mockMvc
        .perform(
            delete("/credit-applications/" + creditApplicationId)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CREDIT_ORGANIZATION))
        .andExpect(status().isNoContent());
    assertTrue(creditApplicationRepository.findById(creditApplicationId).isEmpty());
  }

  @ParameterizedTest
  @EnumSource(value = UserRole.class, mode = EnumSource.Mode.EXCLUDE, names = "CREDIT_ORGANIZATION")
  void testDeleteCreditApplicationById_forbidden(UserRole requesterRole) throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplication.setCreditOrganizationId(requesterId);
    creditApplicationRepository.save(creditApplication);

    mockMvc
        .perform(
            delete("/credit-applications/" + creditApplication.getId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @Test
  void testDeleteCreditApplicationById_creditOrganizationDeletesForeignCreditApplication_forbidden()
      throws Exception {
    CreditApplication creditApplication = testUtils.getTestCreditApplication();
    creditApplicationRepository.save(creditApplication);

    mockMvc
        .perform(
            delete("/credit-applications/" + creditApplication.getId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CREDIT_ORGANIZATION))
        .andExpect(status().isForbidden());
  }

  @Test
  void testDeleteCreditApplicationById_notFound() throws Exception {
    mockMvc
        .perform(
            delete("/credit-applications/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, UserRole.CREDIT_ORGANIZATION))
        .andExpect(status().isNotFound());
  }
}
