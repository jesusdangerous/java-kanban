package project.manager;

import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        }
        return null;
    }

    @Override
    public void addNewTask(Task task) {
        if (task != null) {
            if (task.getId() == null) {
                Integer id = getGeneratedId();
                task.setId(id);
            }
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Ошибка: попытка добавить пустую задачу");
        }
    }

    @Override
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

    @Override
    public void deleteTask(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Ошибка: задача с таким id не существует");
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                if (subtask.getId() == null) {
                    subtask.setId(getGeneratedId());
                }
                subtasks.put(subtask.getId(), subtask);
                epic.addSubtask(subtask.getId());
                updateEpicStatus(epic);
            } else {
                System.out.println("Ошибка: такой эпик не обнаружен");
            }
        } else {
            System.out.println("Ошибка: попытка добавить пустую подзадачу");
        }
    }

    @Override
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

    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            Epic epic = epics.get(subtasks.remove(id).getEpicId());
            epic.deleteSubtask(id);
            updateEpicStatus(epic);
            historyManager.remove(id);
        } else {
            System.out.println("Ошибка: такой id не найден");
        }
    }

    @Override
    public List<Subtask> getSubtasksInEpic(Epic epic) {
        List<Integer> subtasksById = epic.getSubtasks();
        return getSubtasksInEpic(subtasksById);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
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

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void addNewEpic(Epic epic) {
        if (epic != null) {
            if (epic.getId() == null) {
                epic.setId(getGeneratedId());
            }
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Ошибка: попытка добавить пустой эпик");
        }
    }

    @Override
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

    @Override
    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            List<Integer> subtasksById = epic.getSubtasks();
            for (Integer subtaskId : subtasksById) {
                subtasks.remove(subtaskId);
            }
            historyManager.remove(id);
        } else {
            System.out.println("Ошибка: эпик с таким id не существует");
        }
    }

    private Integer getGeneratedId() {
        return ++id;
    }

    @Override
    public List<Subtask> getSubtasksInEpic(List<Integer> subtasksById) {
        List<Subtask> subtasksInEpic = new ArrayList<>();
        for (Integer id : subtasksById) {
            subtasksInEpic.add(subtasks.get(id));
        }
        return subtasksInEpic;
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }
        return null;
    }
}
