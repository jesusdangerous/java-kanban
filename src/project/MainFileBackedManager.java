package project;

import project.manager.FileBackedTaskManager;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

import java.io.File;

public class MainFileBackedManager {

    public static void main(String[] args) {
        File file = new File("resources/memory.csv");

        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        Task task1 = new Task("Выполнить дела по дому", "Сделать генеральную уборку", Status.NEW);
        Task task2 = new Task("Заняться спортом", "Подтянуться 10 раз", Status.NEW);
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Сделать домашнее задание", "Выполнить физику и математику", Status.NEW);
        taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask("Выполнить физику", "Сделать лабу", Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Выполнить математику", "Сделать рассчетную", Status.NEW, epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("-----------------------------------------");

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        System.out.println(loadedManager.getAllTasks());
        System.out.println(loadedManager.getAllEpics());
        System.out.println(loadedManager.getAllSubtasks());
    }
}
