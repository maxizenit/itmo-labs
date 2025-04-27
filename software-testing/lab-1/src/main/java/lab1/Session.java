package lab1;

import java.time.LocalDateTime;

/**
 * Класс сессии пользователя.
 */
public record Session(LocalDateTime loginTime, LocalDateTime logoutTime) {
}
