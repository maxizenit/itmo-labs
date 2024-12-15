package com.vk.itmo.projecttracker.v1;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProjectManagementSystemTest {

    private static final String BASE_URL = "http://localhost:8080/api/v1";

    private static HttpClient client;
    private static String managerToken;
    private static String developerToken;
    private static String testerToken;
    private static String teamLeaderToken;
    private static String otherUserToken;

    private static String projectId;
    private static String milestoneId;
    private static String ticketId;
    private static String bugReportId;

    @BeforeAll
    public void setup() throws Exception {
        client = HttpClient.newHttpClient();

        // Регистрация и авторизация пользователей
        managerToken = registerAndLogin("managerUser", "password");
        developerToken = registerAndLogin("developerUser", "password");
        testerToken = registerAndLogin("testerUser", "password");
        teamLeaderToken = registerAndLogin("teamLeaderUser", "password");
        otherUserToken = registerAndLogin("otherUser", "password");
    }

    @Test
    @Order(1)
    public void testCreateProject() throws Exception {
        // Создание проекта менеджером
        projectId = createProject(managerToken, "New Project", "Project Description");
        assertNotNull(projectId, "Project ID should not be null");
    }

    @Test
    @Order(2)
    public void testAssignTeamLeader() throws Exception {
        // Назначение тимлидера
        boolean success = assignTeamLeader(managerToken, projectId, "teamLeaderUser");
        assertTrue(success, "Team leader should be assigned successfully");
    }

    @Test
    @Order(3)
    public void testAddDeveloperAndTester() throws Exception {
        // Добавление разработчика и тестировщика к проекту
        boolean devAdded = addDeveloper(managerToken, projectId, "developerUser");
        boolean testerAdded = addTester(managerToken, projectId, "testerUser");
        assertTrue(devAdded, "Developer should be added successfully");
        assertTrue(testerAdded, "Tester should be added successfully");
    }

    @Test
    @Order(4)
    public void testCreateMilestone() throws Exception {
        // Создание майлстоуна
        milestoneId = createMilestoneWithCheckSuccess(managerToken, projectId, "Milestone 1", "2024-01-01", "2024-02-01");
        assertNotNull(milestoneId, "Milestone ID should not be null");
    }

    @Test
    @Order(5)
    public void testCreateAndAssignTicket() throws Exception {
        // Тимлидер создает тикет и назначает разработчика
        ticketId = checkSuccessCreateTicket(teamLeaderToken, milestoneId, "Implement feature X", "Details about feature X");
        assertNotNull(ticketId, "Ticket ID should not be null");

        boolean assigned = assignTicket(teamLeaderToken, ticketId, "developerUser");
        assertTrue(assigned, "Ticket should be assigned successfully");
    }

    @Test
    @Order(6)
    public void testUpdateTicketStatus() throws Exception {
        // Разработчик обновляет статус тикета
        boolean statusUpdated1 = updateTicketStatus(developerToken, ticketId, "in_progress");
        boolean statusUpdated2 = updateTicketStatus(developerToken, ticketId, "completed");
        assertTrue(statusUpdated1, "Ticket status should be updated to in_progress");
        assertTrue(statusUpdated2, "Ticket status should be updated to completed");
    }

    @Test
    @Order(7)
    public void testCheckTicketCompletion() throws Exception {
        // Менеджер проверяет выполнение тикета
        String ticketStatus = getTicketStatus(managerToken, ticketId);
        assertEquals("completed", ticketStatus, "Ticket status should be 'completed'");
    }

    @Test
    @Order(8)
    public void testCreateBugReport() throws Exception {
        // Тестировщик тестирует проект и создает сообщение об ошибке
        boolean tested = checkSuccessTestProject(testerToken, projectId);
        assertTrue(tested, "Project should be tested successfully");

        bugReportId = createBugReport(testerToken, projectId, "Found a bug in feature X");
        assertNotNull(bugReportId, "Bug report ID should not be null");
    }

    @Test
    @Order(9)
    public void testFixBugReport() throws Exception {
        // Разработчик исправляет ошибку
        boolean statusUpdated = updateBugReportStatus(developerToken, bugReportId, "fixed");
        assertTrue(statusUpdated, "Bug report status should be updated to 'fixed'");
    }

    @Test
    @Order(10)
    public void testVerifyBugFix() throws Exception {
        // Тестировщик проверяет исправление
        boolean tested = updateBugReportStatus(testerToken, bugReportId, "tested");
        boolean closed = updateBugReportStatus(testerToken, bugReportId, "closed");
        assertTrue(tested, "Bug report status should be updated to 'tested'");
        assertTrue(closed, "Bug report status should be updated to 'closed'");
    }

    @Test
    @Order(11)
    public void testCloseMilestone() throws Exception {
        // Менеджер закрывает майлстоун
        boolean statusUpdated = updateMilestoneStatus(managerToken, milestoneId, "closed");
        assertTrue(statusUpdated, "Milestone status should be updated to 'closed'");
    }

    @Test
    @Order(12)
    public void testDeveloperAssignTicket() throws Exception {
        // Разработчик пытается назначить тикет другому разработчику
        boolean assigned = assignTicket(developerToken, ticketId, "otherUser");
        assertFalse(assigned, "Developer should not be able to assign tickets");
    }

    @Test
    @Order(13)
    public void testTesterCreateMilestone() throws Exception {
        // Тестировщик пытается создать майлстоун
        HttpResponse<String> response = sendCreateMilestoneRequest(testerToken, projectId, "Invalid Milestone", "2024-03-01", "2024-04-01");
        assertEquals(403, response.statusCode(), "Tester should not be able to create milestones");
    }

    @Test
    @Order(14)
    public void testAccessNonexistentProject() throws Exception {
        // Тестировщик пытается получить доступ к несуществующему проекту
        HttpResponse<String> response = testProject(testerToken, "InvalidId");
        assertEquals(404, response.statusCode(), "Should receive 404 Not Found for nonexistent project");
    }

    @Test
    @Order(15)
    public void testAccessProjectUnauthorized() throws Exception {
        HttpResponse<String> response = testProject(otherUserToken, projectId);
        assertEquals(403, response.statusCode(), "User should not have access to the project");
    }

    @Test
    @Order(16)
    public void testUpdateBugReportWithoutAccess() throws Exception {
        // Пользователь пытается обновить сообщение об ошибке, к которому не имеет доступа
        boolean statusUpdated = updateBugReportStatus(otherUserToken, bugReportId, "closed");
        assertFalse(statusUpdated, "User should not be able to update bug reports they do not have access to");
    }

    @Test
    @Order(17)
    public void testCreateTicketInClosedMilestone() throws Exception {
        // Попытка создать тикет в закрытом майлстоуне
        HttpResponse<String> response = createTicket(teamLeaderToken, milestoneId, "New Task", "Details");
        assertEquals(403, response.statusCode(), "Should not be able to create ticket in a closed milestone");
    }

    @Test
    @Order(18)
    public void testManagerPerformDeveloperAction() throws Exception {
        // Менеджер пытается выполнить действие разработчика (например, исправить баг)
        boolean statusUpdated = updateBugReportStatus(managerToken, bugReportId, "fixed");
        assertFalse(statusUpdated, "Manager should not be able to perform developer actions");
    }

    private static String registerAndLogin(String username, String password) throws Exception {
        JSONObject registerBody = new JSONObject()
                .put("username", username)
                .put("password", password)
                .put("email", username + "@example.com");
        HttpRequest registerRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(registerBody.toString()))
                .build();
        HttpResponse<String> registerResponse = client.send(registerRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, registerResponse.statusCode(), "User should be registered successfully");

        JSONObject loginBody = new JSONObject()
                .put("username", username)
                .put("password", password);
        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(loginBody.toString()))
                .build();
        HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, loginResponse.statusCode(), "User should be logged in successfully");

        JSONObject loginJson = new JSONObject(loginResponse.body());
        return getValueOrNull(loginJson, "token");
    }

    private static String createProject(String token, String name, String description) throws Exception {
        JSONObject body = new JSONObject()
                .put("projectName", name)
                .put("description", description);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Project should be created successfully");

        JSONObject json = new JSONObject(response.body());
        return getValueOrNull(json, "projectId");
    }

    private static boolean assignTeamLeader(String token, String projectId, String username) throws Exception {
        JSONObject body = new JSONObject()
                .put("userId", username);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + projectId + "/teamleader"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private static boolean addDeveloper(String token, String projectId, String username) throws Exception {
        JSONObject body = new JSONObject()
                .put("userId", username);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + projectId + "/developers"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private static boolean addTester(String token, String projectId, String username) throws Exception {
        JSONObject body = new JSONObject()
                .put("userId", username);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + projectId + "/testers"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private static String createMilestoneWithCheckSuccess(String token, String projectId, String name, String startDate, String endDate) throws Exception {
        HttpResponse<String> response = sendCreateMilestoneRequest(token, projectId, name, startDate, endDate);
        assertEquals(201, response.statusCode(), "Milestone should be created successfully");

        JSONObject json = new JSONObject(response.body());
        return getValueOrNull(json, "milestoneId");
    }

    private static HttpResponse<String> sendCreateMilestoneRequest(String token,
                                                                   String projectId,
                                                                   String name,
                                                                   String startDate,
                                                                   String endDate) throws IOException, InterruptedException {
        JSONObject body = new JSONObject()
                .put("name", name)
                .put("startDate", startDate)
                .put("endDate", endDate);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + projectId + "/milestones"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String checkSuccessCreateTicket(String token, String milestoneId, String title, String description) throws Exception {
        HttpResponse<String> response = createTicket(token, milestoneId, title, description);
        assertEquals(201, response.statusCode(), "Ticket should be created successfully");

        JSONObject json = new JSONObject(response.body());
        return getValueOrNull(json, "ticketId");
    }

    private static HttpResponse<String> createTicket(String token, String milestoneId, String title, String description) throws IOException, InterruptedException {
        JSONObject body = new JSONObject()
                .put("title", title)
                .put("description", description);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/milestones/" + milestoneId + "/tickets"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static boolean assignTicket(String token, String ticketId, String username) throws Exception {
        JSONObject body = new JSONObject()
                .put("userId", username);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tickets/" + ticketId + "/assign"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private static boolean updateTicketStatus(String token, String ticketId, String status) throws Exception {
        JSONObject body = new JSONObject()
                .put("status", status);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tickets/" + ticketId + "/status"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private static String getTicketStatus(String token, String ticketId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tickets/" + ticketId + "/status"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Should retrieve ticket status successfully");

        JSONObject json = new JSONObject(response.body());
        return getValueOrNull(json, "status");
    }

    private static boolean checkSuccessTestProject(String token, String projectId) throws Exception {
        HttpResponse<String> response = testProject(token, projectId);
        return response.statusCode() == 200;
    }

    private static HttpResponse<String> testProject(String token, String projectId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + projectId + "/test"))
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static String createBugReport(String token, String projectId, String description) throws Exception {
        JSONObject body = new JSONObject()
                .put("description", description);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/projects/" + projectId + "/bugreports"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Bug report should be created successfully");

        JSONObject json = new JSONObject(response.body());
        return getValueOrNull(json, "bugReportId");
    }

    private static boolean updateBugReportStatus(String token, String bugReportId, String status) throws Exception {
        JSONObject body = new JSONObject()
                .put("status", status);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/bugreports/" + bugReportId + "/status"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private static boolean updateMilestoneStatus(String token, String milestoneId, String status) throws Exception {
        JSONObject body = new JSONObject()
                .put("status", status);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/milestones/" + milestoneId + "/status"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }

    private static String getValueOrNull(JSONObject json, String key) {
        try {
            return json.getString(key);
        } catch (JSONException ignored) {
            return null;
        }
    }
}
