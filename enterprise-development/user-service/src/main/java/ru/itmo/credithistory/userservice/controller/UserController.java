package ru.itmo.credithistory.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itmo.credithistory.userservice.dto.UserDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateUserEmailRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateUserPasswordRqDto;
import ru.itmo.credithistory.userservice.dto.rq.UpdateUserRoleRqDto;
import ru.itmo.credithistory.userservice.entity.User;
import ru.itmo.credithistory.userservice.hateoas.UserAssembler;
import ru.itmo.credithistory.userservice.service.UserService;

@Tag(name = "Пользователи")
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@NullMarked
public class UserController {

  private final UserService userService;
  private final UserAssembler userAssembler;

  @Operation(
      summary = "Получение пользователя",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Пользователь"),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content)
      })
  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<UserDto>> getUserById(@PathVariable UUID id) {
    User user = userService.getUserById(id);
    EntityModel<UserDto> entityModel = userAssembler.toModel(user);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Получение всех пользователей",
      responses = @ApiResponse(responseCode = "200", description = "Пользователи"))
  @GetMapping
  public ResponseEntity<CollectionModel<EntityModel<UserDto>>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    CollectionModel<EntityModel<UserDto>> collectionModel = userAssembler.toCollectionModel(users);
    return ResponseEntity.ok(collectionModel);
  }

  @Operation(
      summary = "Обновление e-mail пользователя",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "E-mail пользователя обновлён"),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content)
      })
  @PutMapping("/{id}/email")
  public ResponseEntity<EntityModel<UserDto>> updateUserEmailById(
      @PathVariable UUID id, @RequestBody @Valid UpdateUserEmailRqDto updateDto) {
    User user = userService.updateUserEmailById(id, updateDto.getEmail());
    EntityModel<UserDto> entityModel = userAssembler.toModel(user);
    return ResponseEntity.ok(entityModel);
  }

  @Operation(
      summary = "Обновление пароля пользователя",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "204", description = "Пароль пользователя обновлён"),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content)
      })
  @PutMapping("/{id}/password")
  public ResponseEntity<?> updateUserPasswordById(
      @PathVariable UUID id, @RequestBody @Valid UpdateUserPasswordRqDto updateDto) {
    userService.updateUserPasswordById(id, updateDto.getPassword());
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Обновление роли пользователя",
      parameters =
          @Parameter(
              in = ParameterIn.PATH,
              name = "id",
              description = "Идентификатор пользователя",
              example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482",
              required = true),
      responses = {
        @ApiResponse(responseCode = "200", description = "Роль пользователя обновлена"),
        @ApiResponse(
            responseCode = "400",
            description = "Новая роль некорректна или не может быть применена",
            content = @Content),
        @ApiResponse(
            responseCode = "404",
            description = "Пользователь не найден",
            content = @Content)
      })
  @PutMapping("/{id}/role")
  public ResponseEntity<EntityModel<UserDto>> updateUserRoleById(
      @PathVariable UUID id, @RequestBody @Valid UpdateUserRoleRqDto updateDto) {
    User user = userService.updateUserRoleById(id, updateDto.getRole());
    EntityModel<UserDto> entityModel = userAssembler.toModel(user);
    return ResponseEntity.ok(entityModel);
  }
}
