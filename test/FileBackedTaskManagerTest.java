import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.manager.FileBackedTaskManager;

import project.taskStatus.Status;
import project.taskType.Task;

import java.io.File;
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
}
