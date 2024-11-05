import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.manager.HttpTaskServer;
import project.manager.InMemoryTaskManager;
import project.manager.Managers;
import project.manager.TaskManager;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServerTest {
    private HttpTaskServer httpServer;
    private TaskManager taskManager;
    private Gson gson;
    HttpClient client;
    private final String URL = "http://localhost:8080";

    @BeforeEach
    void create() throws IOException {
        taskManager = new InMemoryTaskManager();
        httpServer = new HttpTaskServer(taskManager);
        gson = Managers.getGson();
        client = HttpClient.newHttpClient();

        httpServer.startServer();
    }

    @AfterEach
    void closeServer() {
        httpServer.stopServer();
    }

    @Test
    void testHandlePostAddTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);

        String taskToJson = gson.toJson(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Task expectedTask = new Task("Задача1", "Сделать1", Status.NEW);
        expectedTask.setId(1);
        Assertions.assertEquals(expectedTask, taskManager.getTaskById(1));
    }

    @Test
    void testHandlePostUpdateTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);

        String taskToJson1 = gson.toJson(task1);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson1))
                .build();

        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Task task2 = new Task("Задача1", "Сделать1", Status.NEW);
        task2.setId(1);

        String taskToJson2 = gson.toJson(task2);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson2))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Task expectedTask = new Task("Задача1", "Сделать1", Status.NEW);
        expectedTask.setId(1);

        Assertions.assertEquals(expectedTask, taskManager.getTaskById(1));
    }

    @Test
    void testHandleGetTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/tasks/1"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Task expectedTask = new Task("Задача1", "Сделать1", Status.NEW);
        expectedTask.setId(1);

        Task returnedTask = gson.fromJson(response.body(), Task.class);

        Assertions.assertEquals(expectedTask, returnedTask);
    }

    @Test
    void testHandleGetTasksTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);

        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/tasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Task> returnedTask = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        Assertions.assertEquals(2, returnedTask.size());
    }

    @Test
    void testHandleDeleteTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/tasks/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertNull(taskManager.getTaskById(1));
    }

    @Test
    void testHandlePostAddEpicTest() throws IOException, InterruptedException {
        Task epic1 = new Task("Эпик1", "Сделать1", Status.NEW);

        String epicToJson = gson.toJson(epic1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Epic expectedEpic = new Epic("Эпик1", "Сделать1", Status.NEW);
        expectedEpic.setId(1);
        Assertions.assertEquals(expectedEpic, taskManager.getEpicById(1));
    }

    @Test
    void testHandlePostUpdateEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);

        String epicToJson1 = gson.toJson(epic1);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicToJson1))
                .build();

        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Epic epic2 = new Epic("Эпик1", "Сделать1", Status.NEW);
        epic2.setId(1);

        String taskToJson2 = gson.toJson(epic2);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson2))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Epic expectedTask = new Epic("Эпик1", "Сделать1", Status.NEW);
        expectedTask.setId(1);

        Assertions.assertEquals(expectedTask, taskManager.getEpicById(1));
    }

    @Test
    void testHandleGetEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/epics/1"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Epic expectedTask = new Epic("Задача1", "Сделать1", Status.NEW);
        expectedTask.setId(1);

        Epic returnedTask = gson.fromJson(response.body(), Epic.class);

        Assertions.assertEquals(expectedTask, returnedTask);
    }

    @Test
    void testHandleGetEpicsTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Epic epic2 = new Epic("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/epics"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Epic> returnedTask = gson.fromJson(response.body(), new TypeToken<List<Epic>>(){}.getType());

        Assertions.assertEquals(2, returnedTask.size());
    }

    @Test
    void testHandleGetSubtasksInEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Задача2", "Сделать2", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Задача3", "Сделать3", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/epics/1/subtasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> returnedTask = gson.fromJson(response.body(), new TypeToken<List<Subtask>>(){}.getType());

        Assertions.assertEquals(2, returnedTask.size());
    }

    @Test
    void testHandleDeleteEpicTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertNull(taskManager.getEpicById(1));
    }

    @Test
    void testHandleGetSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Задача2", "Сделать2", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/subtasks/2"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Subtask expectedTask = new Subtask("Задача2", "Сделать2", Status.NEW, epic1.getId());
        expectedTask.setId(2);

        Subtask returnedTask = gson.fromJson(response.body(), Subtask.class);

        Assertions.assertEquals(expectedTask, returnedTask);
    }

    @Test
    void testHandleGetSubtasksTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Задача2", "Сделать2", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Задача3", "Сделать3", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/subtasks"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        List<Subtask> returnedTask = gson.fromJson(response.body(), new TypeToken<List<Subtask>>(){}.getType());

        Assertions.assertEquals(2, returnedTask.size());
    }

    @Test
    void testHandlePostAddSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Задача2", "Сделать2", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        String subtaskToJson = gson.toJson(subtask1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskToJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Subtask expectedTask = new Subtask("Задача2", "Сделать2", Status.NEW, epic1.getId());
        expectedTask.setId(2);
        Assertions.assertEquals(expectedTask, taskManager.getSubtaskById(2));
    }

    @Test
    void testHandlePostUpdateSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Задача2", "Сделать2", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        String subtaskToJson1 = gson.toJson(subtask1);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskToJson1))
                .build();

        HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(201, response.statusCode());

        Subtask subtask2 = new Subtask("Задача3", "Сделать3", Status.NEW, epic1.getId());
        subtask2.setId(2);

        String taskToJson2 = gson.toJson(subtask2);

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskToJson2))
                .build();

        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        Subtask expectedTask = new Subtask("Задача3", "Сделать3", Status.NEW, epic1.getId());
        expectedTask.setId(2);

        Assertions.assertEquals(expectedTask, taskManager.getSubtaskById(2));
    }

    @Test
    void testHandleDeleteSubtaskTest() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Задача2", "Сделать2", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/subtasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());

        Assertions.assertNull(taskManager.getSubtaskById(1));
    }


    @Test
    void testHandleGetHistoryTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);
        Task task3 = new Task("Задача3", "Сделать3", Status.NEW);
        taskManager.addNewTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/history"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Task> returnedHistory = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        Assertions.assertEquals(returnedHistory, taskManager.getHistory());
    }

    @Test
    void testHandleGetPrioritizedTest() throws IOException, InterruptedException {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 10, 0), Duration.ofHours(1));
        taskManager.addNewTask(task1);

        Task task2 = new Task("Задача2", "Описание2", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 11, 0), Duration.ofHours(1));
        taskManager.addNewTask(task2);

        Task task3 = new Task("Задача3", "Описание3", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 12, 0), Duration.ofHours(1));
        taskManager.addNewTask(task3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/prioritized"))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Task> returnedPrioritized = gson.fromJson(response.body(), new TypeToken<List<Task>>(){}.getType());

        Assertions.assertEquals(returnedPrioritized, taskManager.getPrioritizedTasks());
    }
}