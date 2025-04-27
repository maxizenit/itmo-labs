package lab1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserAnalyticsServiceTest {

    private UserAnalyticsService service;

    @BeforeEach
    void init() {
        service = new UserAnalyticsService();
    }

    // Тестирование регистрации пользователя с уникальным ID
    @Test
    void testRegisterUser_withUniqueId_shouldRegisterSuccessfully() {
        // Данные для пользователя
        String userId = "user123";
        String username = "John Doe";

        // Выполняем регистрацию
        boolean isRegistered = service.registerUser(userId, username);

        // Проверяем, что регистрация прошла успешно
        assertTrue(isRegistered, "User should be registered successfully");
    }

    // Тестирование регистрации пользователя с уже существующим ID (граничный случай)
    @Test
    void testRegisterUser_withExistingId_shouldReturnFalse() {
        // Данные для пользователя
        String userId = "user123";
        String username = "John Doe";

        // Регистрируем первого пользователя
        service.registerUser(userId, username);

        // Попытка зарегистрировать второго пользователя с тем же ID
        assertThrows(IllegalArgumentException.class, () -> service.registerUser(userId, "Jane Doe"));
    }

    // Тестирование записи сессии пользователя
    @Test
    void testRecordSession_withValidUser_shouldAddSession() {
        // Регистрируем пользователя
        String userId = "user123";
        service.registerUser(userId, "John Doe");

        // Данные для сессии
        LocalDateTime loginTime = LocalDateTime.of(2025, 2, 18, 10, 0);
        LocalDateTime logoutTime = LocalDateTime.of(2025, 2, 18, 12, 0);

        // Записываем сессию
        service.recordSession(userId, loginTime, logoutTime);

        // Проверяем, что сессия добавлена
        assertEquals(1, service.getUserSessions().get(userId).size(), "Session should be added");
    }

    // Тестирование записи сессии для несуществующего пользователя
    @Test
    void testRecordSession_withNonExistentUser_shouldThrowException() {
        // Данные для сессии
        String userId = "user123";
        LocalDateTime loginTime = LocalDateTime.of(2025, 2, 18, 10, 0);
        LocalDateTime logoutTime = LocalDateTime.of(2025, 2, 18, 12, 0);

        // Проверяем, что выбрасывается исключение для несуществующего пользователя
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.recordSession(userId, loginTime, logoutTime);
        });

        // Проверяем сообщение исключения
        assertEquals("User not found", exception.getMessage());
    }

    // // // // // Ниже - тесты для лабораторной // // // // //

    private void registerUsers(String... userIds) {
        Arrays.stream(userIds).forEach(userId -> service.registerUser(userId, userId));
    }

    @Test
    void getTotalActivityTimeTest() {
        // Тест без сессий - должно выброситься исключение
        String userId = "user1";
        registerUsers(userId);
        assertThrows(IllegalArgumentException.class, () -> service.getTotalActivityTime(userId));

        // Тест с одной сессией на 2 минуты
        LocalDateTime loginTime = LocalDateTime.of(2025, 2, 24, 3, 0);
        LocalDateTime logoutTime = LocalDateTime.of(2025, 2, 24, 3, 2);
        service.recordSession(userId, loginTime, logoutTime);

        assertEquals(2, service.getTotalActivityTime(userId));

        // Тест с несколькими сессиями (2 из прошлого теста + 60 + 5 = 67)
        loginTime = LocalDateTime.of(2025, 2, 24, 5, 30);
        logoutTime = LocalDateTime.of(2025, 2, 24, 6, 30);
        service.recordSession(userId, loginTime, logoutTime);

        loginTime = LocalDateTime.of(2025, 2, 24, 7, 25);
        logoutTime = LocalDateTime.of(2025, 2, 24, 7, 30);
        service.recordSession(userId, loginTime, logoutTime);

        assertEquals(67, service.getTotalActivityTime(userId));
    }

    @Test
    void findInactiveUsersTest() {
        // Тест без пользователей - должен вернуться пустой список
        List<String> inactiveUsers = service.findInactiveUsers(5);
        assertNotNull(inactiveUsers);
        assertThat(inactiveUsers).isEmpty();

        // Регистрация тестовых пользователей
        String user1Id = "user1";
        String user2Id = "user2";
        String user3Id = "user3";
        registerUsers(user1Id, user2Id, user3Id);

        // Тест без пользовательских сессий
        assertThat(service.findInactiveUsers(1)).isEmpty();

        // Тест, в котором у каждого пользователя по одной очень давней сессии - должны вернуться все пользователи
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime loginTime = LocalDateTime.MIN;
        LocalDateTime logoutTime = LocalDateTime.MIN;

        // Записываем каждому пользователю максимально раннюю сессию, чтобы потом проверить, что сессии сортируются корректно
        service.recordSession(user1Id, loginTime, logoutTime);
        service.recordSession(user2Id, loginTime, logoutTime);
        service.recordSession(user3Id, loginTime, logoutTime);

        inactiveUsers = service.findInactiveUsers(1);
        assertThat(inactiveUsers).containsExactlyInAnyOrder(user1Id, user2Id, user3Id);

        // Тест, при котором user1 и user2 были активны недавно - должен вернуться user3
        loginTime = now.minusDays(2);
        logoutTime = now.minusDays(1);

        service.recordSession(user1Id, loginTime, logoutTime);
        service.recordSession(user2Id, loginTime, logoutTime);

        assertThat(service.findInactiveUsers(2)).containsExactlyInAnyOrder(user3Id);

        // Тест, при котором у user3 были удалены все сессии
        service.getUserSessions().get(user3Id).clear();
        assertThat(service.findInactiveUsers(2)).isEmpty();
    }

    @Test
    void getMonthlyActivityMetricTest() {
        // Тест без сессий - должно выброситься исключение
        String userId = "user123";
        registerUsers(userId);
        assertThrows(IllegalArgumentException.class, () -> service.getMonthlyActivityMetric(userId, YearMonth.of(2025, 2)));

        // Тест с одной сессией вне искомого месяца
        LocalDateTime loginTime = LocalDateTime.of(2025, 2, 24, 3, 0);
        LocalDateTime logoutTime = LocalDateTime.of(2025, 2, 24, 3, 2);
        service.recordSession(userId, loginTime, logoutTime);

        Map<String, Long> metric = service.getMonthlyActivityMetric(userId, YearMonth.of(2025, 1));
        assertThat(metric).isEmpty();

        // Тест, при котором есть 2 сессии в один день месяца, 1 - в другой
        // 10 + 5 минут 20го числа
        loginTime = LocalDateTime.of(2024, 12, 20, 15, 40);
        logoutTime = LocalDateTime.of(2024, 12, 20, 15, 50);
        service.recordSession(userId, loginTime, logoutTime);

        loginTime = LocalDateTime.of(2024, 12, 20, 16, 10);
        logoutTime = LocalDateTime.of(2024, 12, 20, 16, 15);
        service.recordSession(userId, loginTime, logoutTime);

        // 5 минут 21 числа
        loginTime = LocalDateTime.of(2024, 12, 21, 22, 10);
        logoutTime = LocalDateTime.of(2024, 12, 21, 22, 15);
        service.recordSession(userId, loginTime, logoutTime);

        metric = service.getMonthlyActivityMetric(userId, YearMonth.of(2024, 12));
        assertThat(metric).containsAllEntriesOf(Map.of("2024-12-20", 15L, "2024-12-21", 5L));
    }
}

