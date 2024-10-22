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
        Assertions.assertEquals(List.of(task2, task3, subtask1), taskManager.getPrioritizedTasks());

        taskManager.deleteAllTasks();
        Assertions.assertEquals(List.of(subtask1), taskManager.getPrioritizedTasks());

        taskManager.deleteAllEpics();
        Assertions.assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldUpdatingTimingVariablesHaveNotErrors() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,
                LocalDateTime.of(2024, 10, 21, 10, 0), Duration.ofHours(1));
        taskManager.addNewTask(task1);

        task1.setStartTime(LocalDateTime.of(2024, 11, 21, 10, 0));
        task1.setDuration(Duration.ofHours(5));
        taskManager.updateTask(task1);

        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 10, 20, 10, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask1);

        subtask1.setStartTime(LocalDateTime.of(2024, 11, 21, 15, 0));
        subtask1.setDuration(Duration.ofHours(5));
        taskManager.updateSubtask(subtask1);

        Assertions.assertEquals(LocalDateTime.of(2024, 11, 21, 10, 0),
                taskManager.getTaskById(task1.getId()).getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2024, 11, 21, 15, 0),
                taskManager.getTaskById(task1.getId()).getEndTime());
        Assertions.assertEquals(Duration.ofHours(5), taskManager.getTaskById(task1.getId()).getDuration());

        Assertions.assertEquals(LocalDateTime.of(2024, 11, 21, 15, 0),
                taskManager.getSubtaskById(subtask1.getId()).getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2024, 11, 21, 20, 0),
                taskManager.getSubtaskById(subtask1.getId()).getEndTime());
        Assertions.assertEquals(Duration.ofHours(5), taskManager.getSubtaskById(subtask1.getId()).getDuration());

        Assertions.assertEquals(LocalDateTime.of(2024, 11, 21, 15, 0),
                taskManager.getEpicById(epic1.getId()).getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2024, 11, 21, 20, 0),
                taskManager.getEpicById(epic1.getId()).getEndTime());
        Assertions.assertEquals(Duration.ofHours(5), taskManager.getEpicById(epic1.getId()).getDuration());
    }

    @Test
    void shouldConsistencyTasksIfDeleteTask() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 10, 0), Duration.ofHours(1));
        taskManager.addNewTask(task1);

        Task task2 = new Task("Задача2", "Описание2", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 11, 0), Duration.ofHours(1));
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 12, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask1);

        Assertions.assertEquals(List.of(task1, task2, subtask1), taskManager.getPrioritizedTasks());

        taskManager.deleteTask(task2.getId());

        Assertions.assertEquals(List.of(task1, subtask1), taskManager.getPrioritizedTasks());

        taskManager.deleteAllSubtasks();

        Assertions.assertEquals(List.of(task1), taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldConsistencyTasksIfUpdateTask() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 10, 0), Duration.ofHours(1));
        taskManager.addNewTask(task1);

        Task task2 = new Task("Задача2", "Описание2", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 11, 0), Duration.ofHours(1));
        taskManager.addNewTask(task2);

        task1.setStartTime(LocalDateTime.of(2024, 10, 22, 16, 0));
        taskManager.updateTask(task1);

        Assertions.assertEquals(List.of(task2, task1), taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldConsistencyTasksIfAddSubtask() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 10, 0), Duration.ofHours(1));
        taskManager.addNewTask(task1);

        Task task2 = new Task("Задача2", "Описание2", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 11, 0), Duration.ofHours(1));
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 12, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask1);

        Task task3 = new Task("Задача3", "Описание3", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 18, 0), Duration.ofHours(1));
        taskManager.addNewTask(task3);

        Subtask subtask2 = new Subtask("Подзача2", "Сделать2", Status.DONE, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 20, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask2);

        Assertions.assertEquals(List.of(task1, task2, subtask1, task3, subtask2), taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldConsistencyTasksIfUpdateSubtask() {
        Task task1 = new Task("Задача1", "Описание1", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 10, 0), Duration.ofHours(1));
        taskManager.addNewTask(task1);

        Task task2 = new Task("Задача2", "Описание2", Status.NEW,
                LocalDateTime.of(2024, 10, 22, 11, 0), Duration.ofHours(1));
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 12, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзача2", "Сделать2", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 20, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask2);

        subtask2.setStartTime(LocalDateTime.of(2024, 10, 22, 2, 0));

        taskManager.updateSubtask(subtask2);

        System.out.println(taskManager.getSubtaskById(subtask2.getId()).getStartTime());

        System.out.println(taskManager.getPrioritizedTasks());

        Assertions.assertEquals(List.of(subtask2, task1, task2, subtask1), taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldConsistencyTasksIfCollision() {
        LocalDateTime startTime = LocalDateTime.of(2024, 10, 22, 10, 0);
        Duration oneHour = Duration.ofHours(1);
        Task task = new Task("Задача1", "Описание1", Status.NEW, startTime, oneHour);
        Task collisionTask = new Task("Задача2", "Описание2", Status.NEW, startTime.plus(oneHour), oneHour);

        taskManager.addNewTask(task);
        taskManager.addNewTask(collisionTask);

        Task taskToUpdate = new Task("Задача1", "Описание1", Status.NEW, startTime.plus(oneHour), oneHour);
        taskToUpdate.setId(task.getId());
        taskManager.updateTask(taskToUpdate);

        Assertions.assertEquals(List.of(task, collisionTask), taskManager.getPrioritizedTasks());
    }

    @Test
    void shouldConsistencySubtasksIfCollision() {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 12, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзача2", "Сделать2", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 15, 0), Duration.ofHours(2));
        taskManager.addNewSubtask(subtask2);

        Subtask subtaskToUpdate1 = new Subtask("Подзача2", "Сделать2", Status.NEW, epic1.getId(),
                LocalDateTime.of(2024, 10, 22, 15, 0), Duration.ofHours(2));
        subtaskToUpdate1.setId(subtask1.getId());
        taskManager.updateSubtask(subtaskToUpdate1);

        Assertions.assertEquals(List.of(subtask1, subtask2), taskManager.getPrioritizedTasks());
    }
}