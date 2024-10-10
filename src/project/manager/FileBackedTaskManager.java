package project.manager;

import project.exceptions.ManagerLoadException;
import project.exceptions.ManagerSaveException;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;
import project.taskType.TaskType;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTask(Integer id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время сохранения");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                TaskType taskType = task.getType();
                if (taskType == TaskType.TASK) {
                    manager.addNewTask(task);
                } else if (taskType == TaskType.EPIC) {
                    manager.addNewEpic((Epic) task);
                } else {
                    manager.addNewSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка во время загрузки");
        }
        return manager;
    }

    private static Task fromString(String line) {
        String[] taskValues = line.split(",");
        Integer id = Integer.parseInt(taskValues[0]);
        TaskType taskType = TaskType.valueOf(taskValues[1]);
        String name = taskValues[2];
        Status status = Status.valueOf(taskValues[3]);
        String description = taskValues[4];

        Task task;
        if (taskType == TaskType.TASK) {
            task = new Task(name, description, status);
            task.setId(id);
        } else if (taskType == TaskType.EPIC) {
            task = new Epic(name, description, status);
            task.setId(id);
        } else {
            Integer epicId = Integer.parseInt(taskValues[5]);
            task = new Subtask(name, description, status, epicId);
            task.setId(id);
        }
        return task;
    }

    private String toString(Task task) {
        TaskType type = task.getType();

        if (type == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%s",
                    subtask.getId(), subtask.getType(), subtask.getName(),
                    subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        } else {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(), task.getType(), task.getName(),
                    task.getStatus(), task.getDescription());
        }
    }
}
