package ru.itmo.credithistory.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.userservice.enm.UserRole;

@Schema(description = "Пользователь")
@Data
@Builder
@Jacksonized
public class UserDto {

  @Schema(
      description = "Идентификатор пользователя",
      example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID id;

  @Schema(description = "E-mail пользователя", example = "example@example.com")
  private String email;

  @Schema(description = "Роль пользователя")
  private UserRole role;
}
