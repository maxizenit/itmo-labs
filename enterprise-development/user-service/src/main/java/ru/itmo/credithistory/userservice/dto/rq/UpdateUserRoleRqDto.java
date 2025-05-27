package ru.itmo.credithistory.userservice.dto.rq;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.itmo.credithistory.userservice.enm.UserRole;

@Schema(description = "Запрос на обновление роли пользователя")
@Data
@Builder
@Jacksonized
public class UpdateUserRoleRqDto {

  @Schema(description = "Роль пользователя", requiredMode = Schema.RequiredMode.REQUIRED)
  @NotNull
  private UserRole role;
}
