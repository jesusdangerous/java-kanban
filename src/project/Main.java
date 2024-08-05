package project;

import project.manager.TaskManager;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

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

        Epic epic2 = new Epic("Нарисовать плакат", "Использовать все краски", Status.IN_PROGRESS);
        taskManager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask("Нарисовать облака", "Использовать баллончик", Status.NEW, epic2.getId());
        taskManager.addNewSubtask(subtask3);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("----------------------------------------");


        task1.setDescription("Сделать мини-уборку");
        task1.setStatus(Status.IN_PROGRESS);

        Subtask newSubtask2 = new Subtask("Выполнить математику", "Сделать рассчетную", Status.DONE, epic1.getId());
        taskManager.addNewSubtask(newSubtask2);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("----------------------------------------");

        taskManager.deleteTask(task1.getId());
        taskManager.deleteAllEpics();

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
    }
}
