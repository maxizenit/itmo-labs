package lab1;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class UserAnalyticsService {

    private final Map<String, User> users = new HashMap<>();
    private final Map<String, List<Session>> userSessions = new HashMap<>();

    /**
     * Регистрация нового пользователя.
     *
     * @param userId   Уникальный идентификатор пользователя.
     * @param userName Имя пользователя.
     * @throws IllegalArgumentException если пользователь с данным ID уже существует.
     */
    public boolean registerUser(String userId, String userName) {
        if (users.containsKey(userId)) {
            throw new IllegalArgumentException("User already exists");
        }
        users.put(userId, new User(userId, userName));
        return true;
    }

    /**
     * Запись сессии пользователя.
     *
     * @param userId     Идентификатор пользователя.
     * @param loginTime  Время входа пользователя в систему.
     * @param logoutTime Время выхода пользователя из системы.
     * @throws IllegalArgumentException если пользователя с данным ID не существует.
     */
    public void recordSession(String userId, LocalDateTime loginTime, LocalDateTime logoutTime) {
        if (!users.containsKey(userId)) {
            throw new IllegalArgumentException("User not found");
        }
        Session session = new Session(loginTime, logoutTime);
        userSessions.computeIfAbsent(userId, k -> new ArrayList<>()).add(session);
    }

    /**
     * Возвращает общее время активности пользователя по всем сессиям в минутах.
     *
     * @param userId Идентификатор пользователя.
     * @return Общее время активности в минутах.
     * @throws IllegalArgumentException если сессии для пользователя не найдены.
     */
    public long getTotalActivityTime(String userId) {
        if (!userSessions.containsKey(userId)) {
            throw new IllegalArgumentException("No sessions found for user");
        }
        return userSessions.get(userId).stream().mapToLong(session -> ChronoUnit.MINUTES.between(session.loginTime(), session.logoutTime())).sum();
    }

    /**
     * Находит пользователей, которые не были активны более заданного числа дней.
     *
     * @param days Количество дней, по истечении которых пользователь считается неактивным.
     * @return Список идентификаторов неактивных пользователей.
     */
    public List<String> findInactiveUsers(int days) {
        List<String> inactiveUsers = new ArrayList<>();
        for (Map.Entry<String, List<Session>> entry : userSessions.entrySet()) {
            String userId = entry.getKey();
            List<Session> sessions = entry.getValue();
            if (sessions.isEmpty()) continue;
            LocalDateTime lastSessionTime = sessions.get(sessions.size() - 1).logoutTime();
            long daysInactive = ChronoUnit.DAYS.between(lastSessionTime, LocalDateTime.now());
            if (daysInactive > days) {
                inactiveUsers.add(userId);
            }
        }
        return inactiveUsers;
    }

    /**
     * Возвращает метрику активности пользователя по дням за месяц.
     *
     * @param userId Идентификатор пользователя.
     * @param month  Месяц для анализа активности.
     * @return Словарь, где ключ — это дата (в формате "yyyy-MM-dd"), а значение — общее время активности пользователя в этот день (в минутах).
     * @throws IllegalArgumentException если сессии для пользователя не найдены.
     */
    public Map<String, Long> getMonthlyActivityMetric(String userId, YearMonth month) {
        if (!userSessions.containsKey(userId)) {
            throw new IllegalArgumentException("No sessions found for user");
        }
        Map<String, Long> activityByDay = new HashMap<>();
        userSessions.get(userId).stream().filter(session -> isSessionInMonth(session, month)).forEach(session -> {
            String dayKey = session.loginTime().toLocalDate().toString();
            long minutes = ChronoUnit.MINUTES.between(session.loginTime(), session.logoutTime());
            activityByDay.put(dayKey, activityByDay.getOrDefault(dayKey, 0L) + minutes);
        });
        return activityByDay;
    }

    /**
     * Проверяет, находится ли сессия в пределах заданного месяца.
     *
     * @param session Сессия для проверки.
     * @param month   Месяц для проверки.
     * @return true, если сессия входит в данный месяц, иначе false.
     */
    private boolean isSessionInMonth(Session session, YearMonth month) {
        LocalDateTime start = session.loginTime();
        return start.getYear() == month.getYear() && start.getMonth() == month.getMonth();
    }
}
