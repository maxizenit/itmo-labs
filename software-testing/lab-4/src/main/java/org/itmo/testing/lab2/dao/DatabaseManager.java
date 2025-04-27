package org.itmo.testing.lab2.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.flywaydb.core.Flyway;
import org.itmo.testing.lab2.service.UserAnalyticsService;

public class DatabaseManager {
  private final String jdbcUrl;
  private final String username;
  private final String password;

  private Flyway flyway;

  public DatabaseManager(String jdbcUrl, String username, String password) {
    this.jdbcUrl = jdbcUrl;
    this.username = username;
    this.password = password;
    initDatabase(jdbcUrl, username, password);
  }

  private void initDatabase(String jdbcUrl, String username, String password) {
    try {
      flyway =
          Flyway.configure()
              .dataSource(jdbcUrl, username, password)
              .locations("classpath:db/migration")
              .cleanDisabled(false)
              .load();
      flyway.migrate();
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize database", e);
    }
  }

  public void cleanDatabase() {
    flyway.clean();
    flyway.migrate();
  }

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(jdbcUrl, username, password);
  }

  public boolean registerUser(String userId, String userName) throws SQLException {
    String sql = "INSERT INTO users (user_id, user_name) VALUES (?, ?)";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, userId);
      pstmt.setString(2, userName);
      pstmt.executeUpdate();
      return true;
    } catch (SQLException e) {
      if (e.getMessage().contains("unique constraint")
          || e.getMessage().contains("Duplicate entry")
          || e.getMessage().contains("Unique index or primary key violation")) {
        throw new SQLException("User already exists", e);
      }
      throw e;
    }
  }

  public boolean userExists(String userId) throws SQLException {
    String sql = "SELECT 1 FROM users WHERE user_id = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, userId);
      try (ResultSet rs = pstmt.executeQuery()) {
        return rs.next();
      }
    }
  }

  public void recordSession(String userId, LocalDateTime loginTime, LocalDateTime logoutTime)
      throws SQLException {
    String sql = "INSERT INTO sessions (user_id, login_time, logout_time) VALUES (?, ?, ?)";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, userId);
      pstmt.setTimestamp(2, Timestamp.valueOf(loginTime));
      pstmt.setTimestamp(3, Timestamp.valueOf(logoutTime));
      pstmt.executeUpdate();
    }
  }

  public List<UserAnalyticsService.Session> getUserSessions(String userId) throws SQLException {
    String sql =
        "SELECT id, user_id, login_time, logout_time FROM sessions WHERE user_id = ? ORDER BY login_time DESC";
    List<UserAnalyticsService.Session> sessions = new ArrayList<>();

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, userId);
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          sessions.add(
              new UserAnalyticsService.Session(
                  rs.getLong("id"),
                  rs.getString("user_id"),
                  rs.getTimestamp("login_time").toLocalDateTime(),
                  rs.getTimestamp("logout_time").toLocalDateTime()));
        }
      }
    }
    return sessions;
  }

  public List<UserAnalyticsService.Session> getUserSessionsInMonth(String userId, YearMonth month)
      throws SQLException {
    LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
    LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

    String sql =
        "SELECT id, user_id, login_time, logout_time FROM sessions "
            + "WHERE user_id = ? AND login_time >= ? AND login_time <= ? "
            + "ORDER BY login_time";

    List<UserAnalyticsService.Session> sessions = new ArrayList<>();

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, userId);
      pstmt.setTimestamp(2, Timestamp.valueOf(startOfMonth));
      pstmt.setTimestamp(3, Timestamp.valueOf(endOfMonth));

      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          sessions.add(
              new UserAnalyticsService.Session(
                  rs.getLong("id"),
                  rs.getString("user_id"),
                  rs.getTimestamp("login_time").toLocalDateTime(),
                  rs.getTimestamp("logout_time").toLocalDateTime()));
        }
      }
    }
    return sessions;
  }

  public UserAnalyticsService.Session getLastUserSession(String userId) throws SQLException {
    String sql =
        "SELECT id, user_id, login_time, logout_time FROM sessions "
            + "WHERE user_id = ? ORDER BY logout_time DESC LIMIT 1";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, userId);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return new UserAnalyticsService.Session(
              rs.getLong("id"),
              rs.getString("user_id"),
              rs.getTimestamp("login_time").toLocalDateTime(),
              rs.getTimestamp("logout_time").toLocalDateTime());
        }
      }
    }
    return null;
  }

  public List<UserAnalyticsService.User> getAllUsers() throws SQLException {
    String sql = "SELECT user_id, user_name FROM users";
    List<UserAnalyticsService.User> users = new ArrayList<>();

    try (Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql)) {

      while (rs.next()) {
        users.add(
            new UserAnalyticsService.User(rs.getString("user_id"), rs.getString("user_name")));
      }
    }
    return users;
  }

  public UserAnalyticsService.User getUser(String userId) throws SQLException {
    String sql = "SELECT user_id, user_name FROM users WHERE user_id = ?";

    try (Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      pstmt.setString(1, userId);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return new UserAnalyticsService.User(rs.getString("user_id"), rs.getString("user_name"));
        }
      }
    }
    return null;
  }
}
