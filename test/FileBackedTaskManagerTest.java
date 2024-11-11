import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.exceptions.ManagerSaveException;
import project.manager.FileBackedTaskManager;

import project.manager.TaskManager;
import project.taskStatus.Status;
import project.taskType.Task;
import project.taskType.TaskType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {
    private FileBackedTaskManager taskManager;
    private File file;

    @Override
    protected TaskManager createTaskManager() throws IOException {
        file = File.createTempFile("test", ".csv");
        return new FileBackedTaskManager(file);
    }

    @BeforeEach
    void create() throws IOException {
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void shouldSaveEmptyFile() throws IOException {
        taskManager.save();
        String firstLine = Files.readString(file.toPath());
        Assertions.assertEquals(firstLine, "id,type,name,status,description,start_time,duration,epic\n");
    }

    @Test
    void shouldFileBackedManagerSaveTasksInFile() throws IOException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW,
                LocalDateTime.of(2022, 1, 22, 22, 31,11),
                Duration.ofSeconds(32423423));
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW,
                LocalDateTime.of(1999, 6, 11, 11, 11, 21),
                Duration.ofSeconds(124321122));
        taskManager.addNewTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        String expectedLine = "id,type,name,status,description,start_time,duration,epic\n" +
                "1,TASK,Задача1,NEW,Сделать1,2022-01-22T22:31:11,PT9006H30M23S,\n" +
                "2,TASK,Задача2,NEW,Сделать2,1999-06-11T11:11:21,PT34533H38M42S,\n";

        Assertions.assertEquals(expectedLine, Files.readString(file.toPath()));
    }

    @Test
    void shouldFileBackedManagerLoadTasksFromFile() throws IOException {
        Task task1 = new Task("Задача1", "Сделать1", Status.NEW,
                LocalDateTime.of(2022, 1, 22, 22, 31,11),
                Duration.ofSeconds(32423423));
        taskManager.addNewTask(task1);
        Task task2 = new Task("Задача2", "Сделать2", Status.NEW,
                LocalDateTime.of(1999, 6, 11, 11, 11, 21),
                Duration.ofSeconds(124321122));
        taskManager.addNewTask(task2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(taskManager.getAllTasks(), loadedManager.getAllTasks());
    }

    @Test
    void shouldTasksFromFileHaveCorrectlyProperties() throws IOException {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(file))) {
            String data = "id,type,name,status,description,start_time,duration,epic\n" +
                    "1,TASK,Выполнить дела по дому,NEW,Сделать генеральную уборку,2022-01-22T22:31:11,PT9006H30M23S,\n" +
                    "3,TASK,Заняться спортом,NEW,Подтянуться 10 раз,1999-06-11T11:11:21,PT34533H38M42S,\n" +
                    "5,EPIC,Сделать домашнее задание,NEW,Выполнить физику и математику,2024-07-01T09:00:00,PT20S\n" +
                    "6,SUBTASK,Выполнить физику,NEW,Сделать лабу,2024-07-01T09:00:00,PT10S,5\n" +
                    "8,SUBTASK,Выполнить математику,NEW,Сделать рассчетную,2024-07-01T09:00:10,PT10S,5";
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
        Assertions.assertEquals("2022-01-22T22:31:11", taskManager.getTaskById(1).getStartTime().toString());
        Assertions.assertEquals("PT9006H30M23S", taskManager.getTaskById(1).getDuration().toString());

        Assertions.assertEquals("Заняться спортом", taskManager.getTaskById(3).getName());
        Assertions.assertEquals("Подтянуться 10 раз", taskManager.getTaskById(3).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getTaskById(3).getStatus());
        Assertions.assertEquals(3, taskManager.getTaskById(3).getId());
        Assertions.assertEquals(TaskType.TASK, taskManager.getTaskById(3).getType());
        Assertions.assertEquals("1999-06-11T11:11:21", taskManager.getTaskById(3).getStartTime().toString());
        Assertions.assertEquals("PT34533H38M42S", taskManager.getTaskById(3).getDuration().toString());

        Assertions.assertEquals("Сделать домашнее задание", taskManager.getEpicById(5).getName());
        Assertions.assertEquals("Выполнить физику и математику", taskManager.getEpicById(5).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getEpicById(5).getStatus());
        Assertions.assertEquals(5, taskManager.getEpicById(5).getId());
        Assertions.assertEquals(TaskType.EPIC, taskManager.getEpicById(5).getType());
        Assertions.assertEquals("2024-07-01T09:00", taskManager.getEpicById(5).getStartTime().toString());
        Assertions.assertEquals("PT20S", taskManager.getEpicById(5).getDuration().toString());
        Assertions.assertEquals("2024-07-01T09:00:20", taskManager.getEpicById(5).getEndTime().toString());
        Assertions.assertEquals(LocalDateTime.of(2024, 7, 1, 9, 0, 0),
                taskManager.getEpicById(5).getStartTime());

        Assertions.assertEquals("Выполнить физику", taskManager.getSubtaskById(6).getName());
        Assertions.assertEquals("Сделать лабу", taskManager.getSubtaskById(6).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getSubtaskById(6).getStatus());
        Assertions.assertEquals(6, taskManager.getSubtaskById(6).getId());
        Assertions.assertEquals(TaskType.SUBTASK, taskManager.getSubtaskById(6).getType());
        Assertions.assertEquals(5, taskManager.getSubtaskById(6).getEpicId());
        Assertions.assertEquals(LocalDateTime.of(2024, 7, 1, 9, 0, 0), taskManager.getSubtaskById(6).getStartTime());
        Assertions.assertEquals("PT10S", taskManager.getSubtaskById(6).getDuration().toString());

        Assertions.assertEquals("Выполнить математику", taskManager.getSubtaskById(8).getName());
        Assertions.assertEquals("Сделать рассчетную", taskManager.getSubtaskById(8).getDescription());
        Assertions.assertEquals(Status.NEW, taskManager.getSubtaskById(8).getStatus());
        Assertions.assertEquals(8, taskManager.getSubtaskById(8).getId());
        Assertions.assertEquals(TaskType.SUBTASK, taskManager.getSubtaskById(8).getType());
        Assertions.assertEquals(5, taskManager.getSubtaskById(8).getEpicId());
        Assertions.assertEquals(LocalDateTime.of(2024, 7, 1, 9, 0, 10), taskManager.getSubtaskById(8).getStartTime());
        Assertions.assertEquals("PT10S", taskManager.getSubtaskById(8).getDuration().toString());
    }
}
