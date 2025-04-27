package org.itmo.testing.lab2.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.itmo.testing.lab2.dao.DatabaseManager;
import org.itmo.testing.lab2.service.UserAnalyticsService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class UserAnalyticsServiceIntegrationTest {

  @Container
  private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

  private static DatabaseManager dbManager;
  private static UserAnalyticsService service;

  @BeforeAll
  static void setUp() {
    dbManager =
        new DatabaseManager(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
    service = new UserAnalyticsService(dbManager);
  }

  @AfterEach
  void cleanDatabase() {
    dbManager.cleanDatabase();
  }

  @AfterAll
  static void tearDown() {
    postgres.close();
  }

  @Test
  void testFindInactiveUsers() throws SQLException {
    service.registerUser("active1", "Active User 1");
    service.registerUser("inactive1", "Inactive User 1");
    service.registerUser("inactive2", "Inactive User 2");

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime tenDaysAgo = now.minusDays(10);
    LocalDateTime thirtyDaysAgo = now.minusDays(30);

    // Активный пользователь с недавней сессией
    service.recordSession("active1", tenDaysAgo.minusHours(1), tenDaysAgo);

    // Неактивные пользователи с давними сессиями
    service.recordSession("inactive1", thirtyDaysAgo.minusHours(2), thirtyDaysAgo);
    service.recordSession("inactive2", thirtyDaysAgo.minusHours(3), thirtyDaysAgo);

    // Проверка поиска неактивных пользователей за последние 15 дней
    List<String> inactiveUsers = service.findInactiveUsers(15);

    // Проверки
    assertEquals(2, inactiveUsers.size());
    assertTrue(inactiveUsers.contains("inactive1"));
    assertTrue(inactiveUsers.contains("inactive2"));
    assertFalse(inactiveUsers.contains("active1"));
  }

  @Test
  void testMonthlyActivityMetric() {
    // Подготовка
    String userId = "user-monthly";
    service.registerUser(userId, "Monthly User");

    YearMonth currentMonth = YearMonth.now();
    LocalDateTime day1Start = currentMonth.atDay(1).atTime(10, 0);
    LocalDateTime day1End = currentMonth.atDay(1).atTime(12, 0);

    LocalDateTime day15Start = currentMonth.atDay(15).atTime(14, 0);
    LocalDateTime day15End = currentMonth.atDay(15).atTime(17, 0);

    // Записываем сессии
    service.recordSession(userId, day1Start, day1End);
    service.recordSession(userId, day15Start, day15End);

    // Действие
    Map<String, Long> monthlyActivity = service.getMonthlyActivityMetric(userId, currentMonth);

    // Проверки
    assertEquals(2, monthlyActivity.size());
    assertEquals(120L, monthlyActivity.get(currentMonth.atDay(1).toString()));
    assertEquals(180L, monthlyActivity.get(currentMonth.atDay(15).toString()));
  }
}
