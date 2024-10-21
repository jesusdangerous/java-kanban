import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.manager.TaskManager;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager() throws IOException;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = createTaskManager();
    }

    @Test
    void shouldEpicStatusNewIfAllSubtasksAreNew() {
        Epic epic = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача1", "Сделать1", Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask1);

        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldEpicStatusDoneIfAllSubtasksAreDone() {
        Epic epic = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача1", "Сделать1", Status.DONE, epic.getId());
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача2", "Сделать2", Status.DONE, epic.getId());
        taskManager.addNewSubtask(subtask2);

        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldEpicStatusInProgressIfSomeSubtasksAreNewAndSomeAreDone() {
        Epic epic = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача1", "Сделать1", Status.NEW, epic.getId());
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача2", "Сделать2", Status.DONE, epic.getId());
        taskManager.addNewSubtask(subtask2);

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldEpicStatusInProgressIfSubtaskIsInProgress() {
        Epic epic = new Epic("Эпик4", "Сделать4", Status.NEW);
        taskManager.addNewEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача1", "Сделать1", Status.IN_PROGRESS, epic.getId());
        taskManager.addNewSubtask(subtask1);

        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldCalculateTaskIntersectionCorrectly() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,
                LocalDateTime.of(2024, 10, 21, 10, 0), Duration.ofHours(2));

        Task task2 = new Task("Задача2", "Описание2", Status.NEW,
                LocalDateTime.of(2024, 10, 21, 11, 0), Duration.ofHours(1));

        Task task3 = new Task("Задача3", "Описание3", Status.NEW,
                LocalDateTime.of(2024, 10, 21, 12, 0), Duration.ofHours(1));

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        Assertions.assertEquals(List.of(task1, task3), taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldInstanceDurationEpicTime() {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.DONE, epic1.getId(),
                LocalDateTime.of(2024, 10, 21, 10, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзача2", "Сделать2", Status.DONE, epic1.getId(),
                LocalDateTime.of(2024, 10, 21, 13, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask2);

        Assertions.assertEquals(LocalDateTime.of(2024, 10, 21, 15, 0),
                epic1.getEndTime());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 21, 10, 0),
                epic1.getStartTime());
        Assertions.assertEquals(Duration.ofHours(4), epic1.getDuration());
    }

    @Test
    void shouldInstanceIfDeleteTaskFromPrioritizedSet() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,
                LocalDateTime.of(2024, 10, 21, 10, 0), Duration.ofHours(1));

        Task task2 = new Task("Задача2", "Описание2", Status.NEW,
                LocalDateTime.of(2024, 10, 21, 11, 0), Duration.ofHours(1));

        Task task3 = new Task("Задача3", "Описание3", Status.NEW,
                LocalDateTime.of(2024, 10, 21, 12, 0), Duration.ofHours(1));

        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.DONE, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 10, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask1);

        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewTask(task3);

        taskManager.deleteTask(task1.getId());
        Assertions.assertEquals(List.of(task2, task3, epic1), taskManager.getPrioritizedTasks());

        taskManager.deleteAllTasks();
        Assertions.assertEquals(List.of(epic1), taskManager.getPrioritizedTasks());

        taskManager.deleteAllEpics();
        Assertions.assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }
}