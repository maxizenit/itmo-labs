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
import ru.itmo.credithistory.userservice.dto.rq.CreateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateCreditOrganizationRqDto;
import ru.itmo.credithistory.userservice.enm.CreditOrganizationType;
import ru.itmo.credithistory.userservice.enm.UserRole;
import ru.itmo.credithistory.userservice.entity.CreditOrganization;
import ru.itmo.credithistory.userservice.repository.CreditOrganizationRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EmbeddedKafka
@Transactional
@NullMarked
class CreditOrganizationControllerTest {

  private final UUID requesterId = UUID.randomUUID();

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TestUtils testUtils;
  @Autowired private CreditOrganizationRepository creditOrganizationRepository;

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testGetCreditOrganizationByUserId_success(UserRole requesterRole) throws Exception {
    CreditOrganization creditOrganization = testUtils.getTestCreditOrganization();
    creditOrganizationRepository.save(creditOrganization);

    mockMvc
        .perform(
            get("/credit-organizations/" + creditOrganization.getUserId())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(creditOrganization.getUserId().toString()))
        .andExpect(jsonPath("$.inn").value("1234567890"))
        .andExpect(jsonPath("$.shortName").value("Банк"))
        .andExpect(jsonPath("$.fullName").value("Банк Тестовый"))
        .andExpect(jsonPath("$.type").value(CreditOrganizationType.BANK.name()));
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testGetCreditOrganizationByUserId_notFound(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            get("/credit-organizations/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }

  @ParameterizedTest
  @EnumSource(UserRole.class)
  void testGetAllCreditOrganizations(UserRole requesterRole) throws Exception {
    CreditOrganization creditOrganization1 = testUtils.getTestCreditOrganization();
    CreditOrganization creditOrganization2 = testUtils.getTestCreditOrganization();

    creditOrganization1.setInn("1111111111");

    creditOrganization2.setInn("2222222222");
    creditOrganization2.setShortName("Микрофинанс");
    creditOrganization2.setFullName("Микрофинанс Тестовый");
    creditOrganization2.setType(CreditOrganizationType.MICROFINANCE);

    creditOrganizationRepository.saveAll(List.of(creditOrganization1, creditOrganization2));

    mockMvc
        .perform(
            get("/credit-organizations")
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$._embedded.creditOrganizationDtoList[*].inn")
                .value(hasItems("1111111111", "2222222222")))
        .andExpect(
            jsonPath("$._embedded.creditOrganizationDtoList[*].shortName")
                .value(hasItems("Банк", "Микрофинанс")))
        .andExpect(
            jsonPath("$._embedded.creditOrganizationDtoList[*].fullName")
                .value(hasItems("Банк Тестовый", "Микрофинанс Тестовый")))
        .andExpect(
            jsonPath("$._embedded.creditOrganizationDtoList[*].type")
                .value(
                    hasItems(
                        CreditOrganizationType.BANK.name(),
                        CreditOrganizationType.MICROFINANCE.name())));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testCreateCreditOrganization_success(UserRole requesterRole) throws Exception {
    CreateCreditOrganizationRqDto request =
        CreateCreditOrganizationRqDto.builder()
            .email(testUtils.getRandomEmail())
            .password("password")
            .inn("1234567890")
            .shortName("Микрофинанс")
            .fullName("Микрофинанс Тестовый")
            .type(CreditOrganizationType.MICROFINANCE)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/credit-organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").isNotEmpty())
        .andExpect(jsonPath("$.inn").value("1234567890"))
        .andExpect(jsonPath("$.shortName").value("Микрофинанс"))
        .andExpect(jsonPath("$.fullName").value("Микрофинанс Тестовый"))
        .andExpect(jsonPath("$.type").value(CreditOrganizationType.MICROFINANCE.name()));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR"})
  void testCreateCreditOrganization_forbidden(UserRole requesterRole) throws Exception {
    CreateCreditOrganizationRqDto request =
        CreateCreditOrganizationRqDto.builder()
            .email(testUtils.getRandomEmail())
            .password("password")
            .inn("1234567890")
            .shortName("Банк")
            .fullName("Банк Тестовый")
            .type(CreditOrganizationType.BANK)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            post("/credit-organizations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testUpdateCreditOrganizationByUserId_success(UserRole requesterRole) throws Exception {
    CreditOrganization creditOrganization = testUtils.getTestCreditOrganization();
    creditOrganizationRepository.save(creditOrganization);

    UpdateCreditOrganizationRqDto request =
        UpdateCreditOrganizationRqDto.builder()
            .inn("0123456789")
            .shortName("Микрофинанс")
            .fullName("Микрофинанс Тестовый")
            .type(CreditOrganizationType.MICROFINANCE)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/credit-organizations/" + creditOrganization.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(creditOrganization.getUserId().toString()))
        .andExpect(jsonPath("$.inn").value("0123456789"))
        .andExpect(jsonPath("$.shortName").value("Микрофинанс"))
        .andExpect(jsonPath("$.fullName").value("Микрофинанс Тестовый"))
        .andExpect(jsonPath("$.type").value(CreditOrganizationType.MICROFINANCE.name()));
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR"})
  void testUpdateCreditOrganizationByUserId_forbidden(UserRole requesterRole) throws Exception {
    CreditOrganization creditOrganization = testUtils.getTestCreditOrganization();
    creditOrganizationRepository.save(creditOrganization);

    UpdateCreditOrganizationRqDto request =
        UpdateCreditOrganizationRqDto.builder()
            .inn("0123456789")
            .shortName("Микрофинанс")
            .fullName("Микрофинанс Тестовый")
            .type(CreditOrganizationType.MICROFINANCE)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/credit-organizations/" + creditOrganization.getUserId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testUpdateCreditOrganizationByUserId_notFound(UserRole requesterRole) throws Exception {
    UpdateCreditOrganizationRqDto request =
        UpdateCreditOrganizationRqDto.builder()
            .inn("0123456789")
            .shortName("Микрофинанс")
            .fullName("Микрофинанс Тестовый")
            .type(CreditOrganizationType.MICROFINANCE)
            .build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(
            put("/credit-organizations/" + UUID.randomUUID())
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
  void testDeleteCreditOrganizationByUserId_success(UserRole requesterRole) throws Exception {
    CreditOrganization creditOrganization = testUtils.getTestCreditOrganization();
    creditOrganizationRepository.save(creditOrganization);
    UUID creditOrganizationUserId = creditOrganization.getUserId();

    mockMvc
        .perform(
            delete("/credit-organizations/" + creditOrganizationUserId)
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNoContent());
    assertTrue(creditOrganizationRepository.findById(creditOrganizationUserId).isEmpty());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"ADMIN", "SUPERVISOR"})
  void testDeleteCreditOrganizationByUserId_forbidden(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            delete("/credit-organizations/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = UserRole.class,
      names = {"ADMIN", "SUPERVISOR"})
  void testDeleteCreditOrganizationByUserId_notFound(UserRole requesterRole) throws Exception {
    mockMvc
        .perform(
            delete("/credit-organizations/" + UUID.randomUUID())
                .header(AuthorizationHeaders.USER_ID_HEADER, requesterId)
                .header(AuthorizationHeaders.USER_ROLE_HEADER, requesterRole))
        .andExpect(status().isNotFound());
  }
}
