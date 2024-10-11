import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.exceptions.ManagerSaveException;
import project.manager.FileBackedTaskManager;

import project.manager.InMemoryTaskManager;
import project.manager.TaskManager;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;
import project.taskType.TaskType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager taskManager;
    private File file;

    @BeforeEach
    void create() throws IOException {
        file = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void shouldSaveEmptyFile() throws IOException {
        taskManager.save();
        String firstLine = Files.readString(file.toPath());
        Assertions.assertEquals(firstLine, "id,type,name,status,description,epic\n");
    }

    @Test
    void shouldFileBackedManagerSaveTasksInFile() throws IOException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        String expectedLine = "id,type,name,status,description,epic\n" +
                "1,TASK,Задача1,NEW,Сделать1,\n" +
                "2,TASK,Задача2,NEW,Сделать2,\n";

        Assertions.assertEquals(expectedLine, Files.readString(file.toPath()));
    }

    @Test
    void shouldFileBackedManagerLoadTasksFromFile() throws IOException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW);
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW);
        taskManager.addNewTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(taskManager.getAllTasks(), loadedManager.getAllTasks());
    }

    @Test
    void shouldTasksFromFileHaveCorrectlyProperties() throws IOException {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            String data = "id,type,name,status,description,epic\n" +
                    "1,TASK,Выполнить дела по дому,NEW,Сделать генеральную уборку,\n" +
                    "3,TASK,Заняться спортом,NEW,Подтянуться 10 раз,\n" +
                    "5,EPIC,Сделать домашнее задание,NEW,Выполнить физику и математику,\n" +
                    "6,SUBTASK,Выполнить физику,NEW,Сделать лабу,5\n" +
                    "8,SUBTASK,Выполнить математику,NEW,Сделать рассчетную,5";
            br.write(data);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка записи данных в файл");
        }

        taskManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals("Выполнить дела по дому", taskManager.getTaskById(1).getName());
        Assertions.assertEquals("Сделать генеральную уборку", taskManager.getTaskById(1).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getTaskById(1).getStatus());
        Assertions.assertEquals(1, taskManager.getTaskById(1).getId());
        Assertions.assertEquals(TaskType.TASK, taskManager.getTaskById(1).getType());

        Assertions.assertEquals("Заняться спортом", taskManager.getTaskById(3).getName());
        Assertions.assertEquals("Подтянуться 10 раз", taskManager.getTaskById(3).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getTaskById(3).getStatus());
        Assertions.assertEquals(3, taskManager.getTaskById(3).getId());
        Assertions.assertEquals(TaskType.TASK, taskManager.getTaskById(3).getType());

        Assertions.assertEquals("Сделать домашнее задание", taskManager.getEpicById(5).getName());
        Assertions.assertEquals("Выполнить физику и математику", taskManager.getEpicById(5).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getEpicById(5).getStatus());
        Assertions.assertEquals(5, taskManager.getEpicById(5).getId());
        Assertions.assertEquals(TaskType.EPIC, taskManager.getEpicById(5).getType());

        Assertions.assertEquals("Выполнить физику", taskManager.getSubtaskById(6).getName());
        Assertions.assertEquals("Сделать лабу", taskManager.getSubtaskById(6).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getSubtaskById(6).getStatus());
        Assertions.assertEquals(6, taskManager.getSubtaskById(6).getId());
        Assertions.assertEquals(TaskType.SUBTASK, taskManager.getSubtaskById(6).getType());
        Assertions.assertEquals(5, taskManager.getSubtaskById(6).getEpicId());

        Assertions.assertEquals("Выполнить математику", taskManager.getSubtaskById(8).getName());
        Assertions.assertEquals("Сделать рассчетную", taskManager.getSubtaskById(8).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getSubtaskById(8).getStatus());
        Assertions.assertEquals(8, taskManager.getSubtaskById(8).getId());
        Assertions.assertEquals(TaskType.SUBTASK, taskManager.getSubtaskById(8).getType());
        Assertions.assertEquals(5, taskManager.getSubtaskById(8).getEpicId());
    }
}
