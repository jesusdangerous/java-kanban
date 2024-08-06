package project.manager;

import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private Integer id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public void addNewTask(Task task) {
        if (task != null) {
            Integer id = getGeneratedId();
            task.setId(id);
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Ошибка: попытка добавить пустую задачу");
        }
    }

    public void updateTask(Task updatedTask) {
        if (updatedTask != null) {
            Integer taskId = updatedTask.getId();
            if (tasks.containsKey(taskId)) {
                tasks.put(taskId, updatedTask);
            } else {
                System.out.println("Ошибка: задача с таким id не существует");
            }
        } else {
            System.out.println("Ошибка: попытка обновить задачу пустым значением");
        }
    }

    public void deleteTask(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Ошибка: задача с таким id не существует");
        }
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasks();
            updateEpicStatus(epic);
        }
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                Integer id = getGeneratedId();
                subtask.setId(id);
                subtasks.put(id, subtask);
                epic.addSubtask(id);
                updateEpicStatus(epic);
            } else {
                System.out.println("Ошибка: такой эпик не обнаружен");
            }
        } else {
            System.out.println("Ошибка: попытка добавить пустую подзадачу");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(epic);
            } else {
                System.out.println("Ошибка: такой эпик не обнаружен");
            }
        } else {
            System.out.println("Ошибка: попытка обновить задачу пустым значением");
        }
    }

    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.remove(id).getEpicId());
            epic.deleteSubtask(id);
            updateEpicStatus(epic);
        } else {
            System.out.println("Ошибка: такой id не найден");
        }
    }

    public List<Subtask> getSubtasksInEpic(Epic epic) {
        List<Integer> subtasksById = epic.getSubtasks();
        return getSubtasksInEpic(subtasksById);
    }

    private void updateEpicStatus(Epic epic) {
        List<Integer> subtasksById = epic.getSubtasks();
        List<Subtask> subtasksInEpic = getSubtasksInEpic(subtasksById);
        boolean allSubtasksInEpicNew = true;
        boolean allSubtasksInEpicDone = true;

        for (Subtask subtask : subtasksInEpic) {
            if (!subtask.getStatus().equals(Status.NEW)) {
                allSubtasksInEpicNew = false;
            }
            if (!subtask.getStatus().equals(Status.DONE)) {
                allSubtasksInEpicDone = false;
            }
        }

        if (subtasksById.isEmpty() || allSubtasksInEpicNew) {
            epic.setStatus(Status.NEW);
        } else if (allSubtasksInEpicDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void addNewEpic(Epic epic) {
        if (epic != null) {
            Integer id = getGeneratedId();
            epic.setId(id);
            epics.put(id, epic);
        } else {
            System.out.println("Ошибка: попытка добавить пустой эпик");
        }
    }

    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic != null) {
            Integer epicId = updatedEpic.getId();
            if (tasks.containsKey(epicId)) {
                Epic epic = epics.get(id);
                epic.setName(updatedEpic.getName());
                epic.setDescription(updatedEpic.getDescription());
            } else {
                System.out.println("Ошибка: эпик с таким id не существует");
            }
        } else {
            System.out.println("Ошибка: попытка обновить эпик пустым значением");
        }
    }

    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            List<Integer> subtasksById = epic.getSubtasks();
            for (Integer subtaskId : subtasksById) {
                subtasks.remove(subtaskId);
            }
        } else {
            System.out.println("Ошибка: эпик с таким id не существует");
        }
    }

    private Integer getGeneratedId() {
        return ++id;
    }

    private List<Subtask> getSubtasksInEpic(List<Integer> subtasksById) {
        List<Subtask> subtasksInEpic = new ArrayList<>();
        for (Integer id : subtasksById) {
            subtasksInEpic.add(subtasks.get(id));
        }
        return subtasksInEpic;
    }
}
