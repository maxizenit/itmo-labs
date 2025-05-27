package ru.itmo.credithistory.userservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.credithistory.userservice.TestUtils;
import ru.itmo.credithistory.userservice.dto.rq.LoginUserRqDto;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EmbeddedKafka
@Transactional
@NullMarked
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private TestUtils testUtils;
  @Autowired private UserRepository userRepository;

  @Test
  @Transactional
  void testLoginUser_success() throws Exception {
    User user = testUtils.getTestUser();
    userRepository.save(user);

    LoginUserRqDto request =
        LoginUserRqDto.builder().email(user.getEmail()).password("password").build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").isString());
  }

  @Test
  void testLoginUser_userNotFound() throws Exception {
    LoginUserRqDto request =
        LoginUserRqDto.builder().email(testUtils.getRandomEmail()).password("password").build();
    String json = objectMapper.writeValueAsString(request);

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void testLoginUser_wrongPassword() throws Exception {
    User user = testUtils.getTestUser();
    userRepository.save(user);

    LoginUserRqDto rq =
        LoginUserRqDto.builder().email(user.getEmail()).password("incorrectpassword").build();
    String json = objectMapper.writeValueAsString(rq);

    mockMvc
        .perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isUnauthorized());
  }
}
