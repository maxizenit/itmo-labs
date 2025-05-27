package ru.itmo.credithistory.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Клиент")
@Data
@Builder
@Jacksonized
public class CustomerDto {

  @Schema(
      description = "Идентификатор пользователя",
      example = "da0773b1-77e6-4f51-a8fa-0f9742d9a482")
  private UUID userId;

  @Schema(description = "ИНН", example = "123456789012")
  private String inn;

  @Schema(description = "Фамилия", example = "Иванов")
  private String lastName;

  @Schema(description = "Имя", example = "Иван")
  private String firstName;

  @Schema(description = "Отчество", example = "Иванович")
  private String middleName;

  @Schema(description = "Дата рождения", example = "2000-01-01")
  private LocalDate birthdate;
}
