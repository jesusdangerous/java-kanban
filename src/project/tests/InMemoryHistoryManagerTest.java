package project.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.manager.InMemoryTaskManager;
import project.manager.TaskManager;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void create() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldInstanceVersionOfTask() {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);

        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());

        List<Task> tasks = new LinkedList<>();

        tasks.add(task1);
        tasks.add(epic1);

        Assertions.assertEquals(tasks, taskManager.getHistory());
    }

    @Test
    void shouldHistoryNotNull() {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);

        Epic epic1 = new Epic("Эпик1", "Сделать1", Status.NEW);
        taskManager.addNewEpic(epic1);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());

        Assertions.assertNotNull(taskManager.getHistory());
    }

    @Test
    void shouldHistoryHasNotLimitCapacity() {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);
        Task task3 = new Task("Задача3", "Сделать3", Status.NEW);
        taskManager.addNewTask(task3);
        Task task4 = new Task("Задача4", "Сделать4", Status.NEW);
        taskManager.addNewTask(task4);
        Task task5 = new Task("Задача5", "Сделать5", Status.NEW);
        taskManager.addNewTask(task5);
        Task task6 = new Task("Задача6", "Сделать6", Status.NEW);
        taskManager.addNewTask(task6);
        Task task7 = new Task("Задача7", "Сделать7", Status.NEW);
        taskManager.addNewTask(task7);
        Task task8 = new Task("Задача8", "Сделать8", Status.NEW);
        taskManager.addNewTask(task8);
        Task task9 = new Task("Задача9", "Сделать9", Status.NEW);
        taskManager.addNewTask(task9);
        Task task10 = new Task("Задача10", "Сделать10", Status.NEW);
        taskManager.addNewTask(task10);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getTaskById(task5.getId());
        taskManager.getTaskById(task6.getId());
        taskManager.getTaskById(task7.getId());
        taskManager.getTaskById(task8.getId());
        taskManager.getTaskById(task9.getId());
        taskManager.getTaskById(task10.getId());
        taskManager.getTaskById(task10.getId());

        Task task11 = new Task("Задача11", "Сделать11", Status.NEW);
        taskManager.addNewTask(task11);
        taskManager.getTaskById(task11.getId());

        Task expectedTask = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(expectedTask);
        expectedTask.setId(1);

        Assertions.assertEquals(expectedTask, taskManager.getHistory().getFirst());
    }

    @Test
    void shouldHistoryHasNotDuplicates() {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);
        Task task3 = new Task("Задача3", "Сделать3", Status.NEW);
        taskManager.addNewTask(task3);
        Task task4 = new Task("Задача4", "Сделать4", Status.NEW);
        taskManager.addNewTask(task4);
        Task task5 = new Task("Задача5", "Сделать5", Status.NEW);
        taskManager.addNewTask(task5);
        Task task6 = new Task("Задача6", "Сделать6", Status.NEW);
        taskManager.addNewTask(task6);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getTaskById(task5.getId());
        taskManager.getTaskById(task6.getId());

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        List<Task> expectedList = new LinkedList<>();
        expectedList.add(task2);
        expectedList.add(task3);
        expectedList.add(task4);
        expectedList.add(task5);
        expectedList.add(task6);
        expectedList.add(task1);

        Assertions.assertEquals(expectedList, taskManager.getHistory());
    }

    @Test
    void shouldHistoryHasPossibilityDeleteTasks() {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);
        Task task3 = new Task("Задача3", "Сделать3", Status.NEW);
        taskManager.addNewTask(task3);
        Task task4 = new Task("Задача4", "Сделать4", Status.NEW);
        taskManager.addNewTask(task4);
        Task task5 = new Task("Задача5", "Сделать5", Status.NEW);
        taskManager.addNewTask(task5);
        Task task6 = new Task("Задача6", "Сделать6", Status.NEW);
        taskManager.addNewTask(task6);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getTaskById(task5.getId());
        taskManager.getTaskById(task6.getId());

        taskManager.deleteTask(task1.getId());

        List<Task> expectedList = new LinkedList<>();
        expectedList.add(task2);
        expectedList.add(task3);
        expectedList.add(task4);
        expectedList.add(task5);
        expectedList.add(task6);

        Assertions.assertEquals(expectedList, taskManager.getHistory());
    }
}
