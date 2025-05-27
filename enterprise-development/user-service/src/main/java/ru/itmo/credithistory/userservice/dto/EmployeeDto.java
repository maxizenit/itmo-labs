package ru.itmo.credithistory.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Сотрудник")
@Data
@Builder
@Jacksonized
public class EmployeeDto {

  @Schema(
      description = "Идентификатор пользователя",
      example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID userId;

  @Schema(description = "Фамилия", example = "Иванов")
  private String lastName;

  @Schema(description = "Имя", example = "Иван")
  private String firstName;

  @Schema(description = "Отчество", example = "Иванович")
  private String middleName;
}
