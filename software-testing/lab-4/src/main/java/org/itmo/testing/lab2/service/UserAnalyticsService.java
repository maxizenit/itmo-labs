package org.itmo.testing.lab2.service;

import org.itmo.testing.lab2.dao.DatabaseManager;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.sql.SQLException;

public class UserAnalyticsService {

    private final DatabaseManager dbManager;

    public UserAnalyticsService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean registerUser(String userId, String userName) {
        try {
            if (dbManager.userExists(userId)) {
                throw new IllegalArgumentException("User already exists");
            }
            return dbManager.registerUser(userId, userName);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while registering user", e);
        }
    }

    public void recordSession(String userId, LocalDateTime loginTime, LocalDateTime logoutTime) {
        try {
            if (!dbManager.userExists(userId)) {
                throw new IllegalArgumentException("User not found");
            }
            dbManager.recordSession(userId, loginTime, logoutTime);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while recording session", e);
        }
    }

    public long getTotalActivityTime(String userId) {
        try {
            List<Session> sessions = dbManager.getUserSessions(userId);
            if (sessions.isEmpty()) {
                throw new IllegalArgumentException("No sessions found for user");
            }
            return sessions.stream()
                    .mapToLong(session -> ChronoUnit.MINUTES.between(session.getLoginTime(), session.getLogoutTime()))
                    .sum();
        } catch (SQLException e) {
            throw new RuntimeException("Database error while getting total activity time", e);
        }
    }

    public List<String> findInactiveUsers(int days) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
            List<String> inactiveUsers = new ArrayList<>();

            List<User> allUsers = dbManager.getAllUsers();
            for (User user : allUsers) {
                Session lastSession = dbManager.getLastUserSession(user.getUserId());
                if (lastSession == null) continue;

                if (lastSession.getLogoutTime().isBefore(cutoffDate)) {
                    inactiveUsers.add(user.getUserId());
                }
            }
            return inactiveUsers;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while finding inactive users", e);
        }
    }

    public Map<String, Long> getMonthlyActivityMetric(String userId, YearMonth month) {
        try {
            List<Session> sessions = dbManager.getUserSessionsInMonth(userId, month);

            if (sessions.isEmpty()) {
                throw new IllegalArgumentException("No sessions found for user in the specified month");
            }

            Map<String, Long> activityByDay = new HashMap<>();
            for (Session session : sessions) {
                String dayKey = session.getLoginTime().toLocalDate().toString();
                long minutes = ChronoUnit.MINUTES.between(session.getLoginTime(), session.getLogoutTime());
                activityByDay.put(dayKey, activityByDay.getOrDefault(dayKey, 0L) + minutes);
            }

            return activityByDay;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while getting monthly activity", e);
        }
    }

    public User getUser(String userId) {
        try {
            User user = dbManager.getUser(userId);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while getting user", e);
        }
    }

    public List<Session> getUserSessions(String userId) {
        try {
            return dbManager.getUserSessions(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while getting user sessions", e);
        }
    }

    public static class User {
        private final String userId;
        private final String userName;

        public User(String userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }
    }


    public static class Session {
        private Long id;
        private final String userId;
        private final LocalDateTime loginTime;
        private final LocalDateTime logoutTime;

        public Session(String userId, LocalDateTime loginTime, LocalDateTime logoutTime) {
            this.userId = userId;
            this.loginTime = loginTime;
            this.logoutTime = logoutTime;
        }

        public Session(Long id, String userId, LocalDateTime loginTime, LocalDateTime logoutTime) {
            this.id = id;
            this.userId = userId;
            this.loginTime = loginTime;
            this.logoutTime = logoutTime;
        }

        public Long getId() {
            return id;
        }

        public String getUserId() {
            return userId;
        }

        public LocalDateTime getLoginTime() {
            return loginTime;
        }

        public LocalDateTime getLogoutTime() {
            return logoutTime;
        }
    }
}
