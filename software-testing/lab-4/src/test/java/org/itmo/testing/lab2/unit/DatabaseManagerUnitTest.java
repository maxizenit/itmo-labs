package org.itmo.testing.lab2.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.itmo.testing.lab2.dao.DatabaseManager;
import org.itmo.testing.lab2.service.UserAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DatabaseManagerUnitTest {

  private DatabaseManager dbManager;

  @BeforeEach
  void setUp() {
    // Инициализация in-memory H2 базы для каждого теста
    dbManager = new DatabaseManager("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
  }

  @Test
  @DisplayName("Тест успешной регистрации пользователя")
  void testRegisterUser() throws SQLException {
    // Act
    boolean result = dbManager.registerUser("test123", "Test User");

    // Assert
    assertTrue(result);
    assertTrue(dbManager.userExists("test123"));

    UserAnalyticsService.User user = dbManager.getUser("test123");
    assertNotNull(user);
    assertEquals("test123", user.getUserId());
    assertEquals("Test User", user.getUserName());
  }

  @Test
  @DisplayName("Тест исключения при повторной регистрации пользователя")
  void testRegisterDuplicateUser() throws SQLException {
    // Arrange
    dbManager.registerUser("duplicate", "Original User");

    // Act & Assert
    SQLException exception =
        assertThrows(
            SQLException.class, () -> dbManager.registerUser("duplicate", "Duplicate User"));

    assertTrue(exception.getMessage().contains("User already exists"));
  }

  @Test
  @DisplayName("Тест записи и получения сессий пользователя")
  void testRecordAndGetSessions() throws SQLException {
    // Arrange
    String userId = "session-user";
    dbManager.registerUser(userId, "Session Test User");

    LocalDateTime login1 = LocalDateTime.of(2025, 1, 1, 10, 0);
    LocalDateTime logout1 = LocalDateTime.of(2025, 1, 1, 11, 30);

    LocalDateTime login2 = LocalDateTime.of(2025, 1, 2, 9, 0);
    LocalDateTime logout2 = LocalDateTime.of(2025, 1, 2, 10, 15);

    // Act
    dbManager.recordSession(userId, login1, logout1);
    dbManager.recordSession(userId, login2, logout2);

    List<UserAnalyticsService.Session> sessions = dbManager.getUserSessions(userId);
    UserAnalyticsService.Session lastSession = dbManager.getLastUserSession(userId);

    // Assert
    assertEquals(2, sessions.size());

    // Проверка последней сессии (сортировка по времени выхода)
    assertNotNull(lastSession);
    assertEquals(userId, lastSession.getUserId());
    assertEquals(login2, lastSession.getLoginTime());
    assertEquals(logout2, lastSession.getLogoutTime());

    // Проверка сессий за январь 2025
    YearMonth january2025 = YearMonth.of(2025, 1);
    List<UserAnalyticsService.Session> januarySessions =
        dbManager.getUserSessionsInMonth(userId, january2025);

    assertEquals(2, januarySessions.size());
  }

  @Test
  @DisplayName("Тест получения сессий пользователя")
  void testGetUserSessions() throws SQLException {
    String userId = "test-get-user-sessions-user";
    dbManager.registerUser(userId, "Test user");

    // У нового пользователя не должно быть сессий
    assertThat(dbManager.getUserSessions(userId)).isEmpty();

    LocalDateTime login1 = LocalDateTime.of(2025, 1, 1, 10, 0);
    LocalDateTime logout1 = LocalDateTime.of(2025, 1, 1, 11, 30);

    LocalDateTime login2 = LocalDateTime.of(2025, 1, 2, 9, 0);
    LocalDateTime logout2 = LocalDateTime.of(2025, 1, 2, 10, 15);

    dbManager.recordSession(userId, login1, logout1);
    dbManager.recordSession(userId, login2, logout2);

    List<UserAnalyticsService.Session> sessions = dbManager.getUserSessions(userId);
    assertThat(sessions).hasSize(2);

    assertThat(sessions)
        .filteredOn(
            session ->
                session.getUserId().equals(userId)
                    && session.getLoginTime().equals(login1)
                    && session.getLogoutTime().equals(logout1))
        .hasSize(1);
    assertThat(sessions)
        .filteredOn(
            session ->
                session.getUserId().equals(userId)
                    && session.getLoginTime().equals(login2)
                    && session.getLogoutTime().equals(logout2))
        .hasSize(1);
  }

  @Test
  @DisplayName("Тест получения последней сессии пользователя")
  void testGetLastUserSessions() throws SQLException {
    String userId = "test-get-last-user-sessions-user";
    dbManager.registerUser(userId, "Test user");

    // У нового пользователя не должно быть сессий
    assertNull(dbManager.getLastUserSession(userId));

    LocalDateTime login1 = LocalDateTime.of(2025, 1, 1, 10, 0);
    LocalDateTime logout1 = LocalDateTime.of(2025, 1, 1, 11, 30);

    LocalDateTime login2 = LocalDateTime.of(2025, 1, 1, 9, 0);
    LocalDateTime logout2 = LocalDateTime.of(2025, 1, 1, 10, 15);

    dbManager.recordSession(userId, login1, logout1);
    dbManager.recordSession(userId, login2, logout2);

    UserAnalyticsService.Session lastSession = dbManager.getLastUserSession(userId);

    assertEquals(login1, lastSession.getLoginTime());
    assertEquals(logout1, lastSession.getLogoutTime());
  }

  @Test
  @DisplayName("Тест получения сессий за определённый месяц")
  void testGetUserSessionsInMonth() throws SQLException {
    String userId = "test-get-user-sessions-in-month";
    dbManager.registerUser(userId, "Test user");

    assertThat(dbManager.getUserSessionsInMonth(userId, YearMonth.of(2025, 4))).isEmpty();

    LocalDateTime login1 = LocalDateTime.of(2025, 2, 1, 10, 0);
    LocalDateTime logout1 = LocalDateTime.of(2025, 2, 1, 11, 30);

    LocalDateTime login2 = LocalDateTime.of(2025, 2, 1, 9, 0);
    LocalDateTime logout2 = LocalDateTime.of(2025, 2, 1, 10, 15);

    LocalDateTime login3 = LocalDateTime.of(2025, 3, 1, 10, 0);
    LocalDateTime logout3 = LocalDateTime.of(2025, 3, 1, 11, 30);

    LocalDateTime login4 = LocalDateTime.of(2025, 4, 1, 9, 0);
    LocalDateTime logout4 = LocalDateTime.of(2025, 4, 1, 10, 15);

    dbManager.recordSession(userId, login1, logout1);
    dbManager.recordSession(userId, login2, logout2);
    dbManager.recordSession(userId, login3, logout3);
    dbManager.recordSession(userId, login4, logout4);

    assertThat(dbManager.getUserSessionsInMonth(userId, YearMonth.of(2025, 2))).hasSize(2);
    assertThat(dbManager.getUserSessionsInMonth(userId, YearMonth.of(2025, 3))).hasSize(1);

    List<UserAnalyticsService.Session> sessions =
        dbManager.getUserSessionsInMonth(userId, YearMonth.of(2025, 4));
    assertThat(sessions).hasSize(1);
    assertEquals(login4, sessions.getFirst().getLoginTime());
    assertEquals(logout4, sessions.getFirst().getLogoutTime());
  }
}
