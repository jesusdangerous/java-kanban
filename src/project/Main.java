package project;

import project.manager.InMemoryTaskManager;
import project.manager.Managers;
import project.manager.TaskManager;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new InMemoryTaskManager();

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

        System.out.println("----------------------------------------");

        Task task5 = new Task("Выполнить дела по дому", "Сделать генеральную уборку", Status.NEW);
        Task task6 = new Task("Заняться спортом", "Подтянуться 10 раз", Status.NEW);
        taskManager.addNewTask(task5);
        taskManager.addNewTask(task6);

        Epic epic7 = new Epic("Сделать домашнее задание", "Выполнить физику и математику", Status.NEW);
        taskManager.addNewEpic(epic7);
        Subtask subtask8 = new Subtask("Выполнить физику", "Сделать лабу", Status.NEW, epic7.getId());
        Subtask subtask9 = new Subtask("Выполнить математику", "Сделать рассчетную", Status.NEW, epic7.getId());
        taskManager.addNewSubtask(subtask8);
        taskManager.addNewSubtask(subtask9);

        Epic epic10 = new Epic("Нарисовать плакат", "Использовать все краски", Status.IN_PROGRESS);
        taskManager.addNewEpic(epic10);
        Subtask subtask11 = new Subtask("Нарисовать облака", "Использовать баллончик", Status.NEW, epic10.getId());
        taskManager.addNewSubtask(subtask11);

        taskManager.getTaskById(task5.getId());
        taskManager.getTaskById(task6.getId());
        taskManager.getEpicById(epic7.getId());

        System.out.println(taskManager.getHistory());

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        System.out.println("----------------------------------------");

        Task task12 = new Task("Выполнить 1", "Сделать 1", Status.NEW);
        Task task13 = new Task("Выполнить 2", "Сделать 2", Status.NEW);

        taskManager.addNewTask(task12);
        taskManager.addNewTask(task13);

        Epic epic14 = new Epic("Нарисовать 3", "Использовать 3", Status.IN_PROGRESS);

        taskManager.addNewEpic(epic14);

        Subtask subtask15 = new Subtask("Выполнить 4", "Сделать 4", Status.NEW, epic14.getId());
        Subtask subtask16 = new Subtask("Выполнить 5", "Сделать 5", Status.NEW, epic14.getId());

        taskManager.addNewSubtask(subtask15);
        taskManager.addNewSubtask(subtask16);

        Epic epic17 = new Epic("Сделать 6", "Выполнить 6", Status.NEW);

        taskManager.addNewEpic(epic17);

        taskManager.getTaskById(task13.getId());
        taskManager.getEpicById(epic17.getId());
        taskManager.getEpicById(epic14.getId());
        taskManager.getSubtaskById(subtask15.getId());
        taskManager.getTaskById(task13.getId());
        taskManager.getTaskById(task12.getId());
        taskManager.getEpicById(epic17.getId());
        taskManager.getTaskById(task12.getId());
        taskManager.getTaskById(task12.getId());

        System.out.println(taskManager.getHistory());
        System.out.println("---");

        taskManager.deleteTask(task13.getId());

        System.out.println(taskManager.getHistory());
        System.out.println("---");

        taskManager.deleteEpicById(epic17.getId());

        System.out.println(taskManager.getHistory());
    }
}
