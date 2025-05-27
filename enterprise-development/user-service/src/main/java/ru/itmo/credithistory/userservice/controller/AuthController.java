package ru.itmo.credithistory.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.credithistory.userservice.dto.TokenDto;
import ru.itmo.credithistory.userservice.dto.rq.LoginUserRqDto;
import ru.itmo.credithistory.userservice.service.AuthService;

@Tag(name = "Аутентификация")
@Validated
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@NullMarked
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "Аутентификация пользователя",
      responses = {
        @ApiResponse(responseCode = "200", description = "JWT-токен аутентификации"),
        @ApiResponse(responseCode = "401", description = "Неверный пароль", content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь с заданным e-mail не найден",
            content = @Content)
      })
  @PostMapping("/login")
  public ResponseEntity<TokenDto> loginUser(@RequestBody @Valid LoginUserRqDto loginDto) {
    String token = authService.generateToken(loginDto.getEmail(), loginDto.getPassword());
    TokenDto response = TokenDto.builder().token(token).build();
    return ResponseEntity.ok(response);
  }
}
