package project.manager;

import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(Integer id);

    void addNewTask(Task task);

    void updateTask(Task updatedTask);

    void deleteTask(Integer id);

    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(Integer id);

    void addNewSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(Integer id);

    List<Subtask> getSubtasksInEpic(Epic epic);

    void updateEpicStatus(Epic epic);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    void addNewEpic(Epic epic);

    Epic getEpicById(Integer id);

    void updateEpic(Epic updatedEpic);

    void deleteEpicById(Integer id);

    List<Subtask> getSubtasksInEpic(List<Integer> subtasksById);
}
