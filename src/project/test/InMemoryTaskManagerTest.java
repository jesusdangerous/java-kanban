package project.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.manager.InMemoryTaskManager;
import project.manager.TaskManager;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void create() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldInstanceTaskEqualWhenIdEqual() {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);
        task2.setId(1);

        Assertions.assertEquals(task1.getId(), task2.getId());
    }

    @Test
    void shouldInstanceEpicEqualWhenIdEqual() {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewTask(epic1);
        Epic epic2 = new Epic("Эпик2", "Сделать2", Status.NEW);
        taskManager.addNewTask(epic2);
        epic2.setId(1);

        Assertions.assertEquals(epic1.getId(), epic2.getId());
    }

    @Test
    void shouldInstanceSubtaskEqualWhenIdEqual() {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Сделать2", Status.NEW);
        taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId());
        epic1.addSubtask(subtask1.getId());

        Subtask subtask2 = new Subtask("Подзача2", "Сделать2", Status.NEW, epic1.getId());
        epic2.addSubtask(subtask2.getId());

        Assertions.assertEquals(subtask1.getId(), subtask2.getId());
    }

    @Test
    void shouldAnotherIdWhenManagerAddTask() {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);

        Assertions.assertNotEquals(task1, task2);
    }

    @Test
    void shouldAnotherIdWhenManagerAddEpic() {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Сделать2", Status.NEW);
        taskManager.addNewEpic(epic2);

        Assertions.assertNotEquals(epic1, epic2);
    }

    @Test
    void shouldAnotherIdWhenManagerAddSubtask() {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Epic epic2 = new Epic("Эпик2", "Сделать2", Status.NEW);
        taskManager.addNewEpic(epic2);

        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId());
        epic1.addSubtask(subtask1.getId());

        Subtask subtask2 = new Subtask("Подзача2", "Сделать2", Status.NEW, epic2.getId());
        epic2.addSubtask(subtask2.getId());

        Assertions.assertNotEquals(subtask1, subtask2);
    }

    @Test
    void shouldTrueIfInstanceCanFindTask() {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);

        Assertions.assertEquals(1, task1.getId());
        Assertions.assertNotNull(taskManager.getAllTasks());
        Assertions.assertNotNull(taskManager.getTaskById(task1.getId()));
        Assertions.assertNotNull(task1.getName());
        Assertions.assertNotNull(task1.getStatus());

        Assertions.assertEquals(task1.getId(), taskManager.getTaskById(task1.getId()).getId());
    }

    @Test
    void shouldTrueIfInstanceCanFindEpic() {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        Assertions.assertEquals(1, epic1.getId());
        Assertions.assertNotNull(taskManager.getAllEpics());
        Assertions.assertNotNull(taskManager.getSubtasksInEpic(epic1));
        Assertions.assertNotNull(epic1.getName());
        Assertions.assertNotNull(epic1.getStatus());

        Assertions.assertEquals(subtask1.getEpicId(), epic1.getId());
    }

    @Test
    void shouldTrueIfInstanceCanFindSubtask() {
        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзача1", "Сделать1", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);

        Assertions.assertEquals(1, subtask1.getEpicId());
        Assertions.assertNotNull(taskManager.getAllSubtasks());
        Assertions.assertNotNull(taskManager.getSubtaskById(subtask1.getId()));
        Assertions.assertNotNull(subtask1.getName());
        Assertions.assertNotNull(subtask1.getStatus());

        Assertions.assertEquals(subtask1.getId(), taskManager.getSubtaskById(subtask1.getId()).getId());
    }

    @Test
    void shouldManagerIsNotNull() {
        Assertions.assertNotNull(taskManager);
    }
}
