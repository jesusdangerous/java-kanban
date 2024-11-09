package project.manager;

import project.exceptions.TaskTimeConflictException;
import project.taskStatus.Status;
import project.taskType.Epic;
import project.taskType.Subtask;
import project.taskType.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {

    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

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
        prioritizedTasks.removeIf(task -> tasks.containsKey(task.getId()));
        tasks.keySet().forEach(historyManager::remove);
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
            addTaskInPrioritizedTasks(task);
        } else {
            System.out.println("Ошибка: попытка добавить пустую задачу");
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        Objects.requireNonNull(updatedTask, "Task for update must not be null");
        var taskId = updatedTask.getId();
        if (!tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("No task by id %s".formatted(taskId));
        }
        var oldTask = tasks.get(taskId);
        prioritizedTasks.removeIf(task -> task.getId().equals(oldTask.getId()));
        if (hasIntersections(updatedTask)) {
            prioritizedTasks.add(oldTask);
            throw new TaskTimeConflictException("Task with id=%s has intersection".formatted(taskId));
        }
        tasks.put(taskId, updatedTask);
        prioritizedTasks.add(updatedTask);
    }

    @Override
    public void deleteTask(Integer id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            throw new IllegalArgumentException("No task by id %s".formatted(id));
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeIf(subTask -> subtasks.containsKey(subTask.getId()));
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.deleteAllSubtasks();
            updateEpicStatus(epic);
        });
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
                updateEpicTime(epic);
                addTaskInPrioritizedTasks(subtask);
            } else {
                throw new IllegalArgumentException("No subtask by id %s".formatted(id));
            }
        } else {
            System.out.println("Ошибка: попытка добавить пустую подзадачу");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Objects.requireNonNull(subtask, "Subtask for update must not be null");
        var subtaskId = subtask.getId();
        if (!subtasks.containsKey(subtaskId)) {
            throw new IllegalArgumentException("No subtask by id %s".formatted(subtaskId));
        }
        var epicId = subtask.getEpicId();
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("No epic by id %s".formatted(subtaskId));
        }
        var epic = epics.get(epicId);
        if (!epic.getSubtasks().contains(subtaskId)) {
            throw new IllegalArgumentException("Subtask with id=%s is not related to epic with id=%s".formatted(subtaskId, epicId));
        }
        var oldSubtask = subtasks.get(subtaskId);
        prioritizedTasks.removeIf(task -> task.getId().equals(oldSubtask.getId()));
        if (hasIntersections(subtask)) {
            prioritizedTasks.add(oldSubtask);
            throw new TaskTimeConflictException("Subtask with id=%s has intersection".formatted(subtaskId));
        }
        subtasks.put(subtaskId, subtask);
        prioritizedTasks.add(subtask);
        updateEpicStatus(epic);
        updateEpicTime(epic);
    }

    private boolean hasIntersections(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(task -> checkIntersections(newTask, task));
    }

    @Override
    public void deleteSubtaskById(Integer id) {
        if (subtasks.containsKey(id)) {
            prioritizedTasks.remove(subtasks.get(id));
            Epic epic = epics.get(subtasks.remove(id).getEpicId());
            epic.deleteSubtask(id);
            updateEpicStatus(epic);
            updateEpicTime(epic);
            historyManager.remove(id);
        } else {
            throw new IllegalArgumentException("No subtask by id %s".formatted(id));
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
        epics.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(epics.get(id));
        });
        subtasks.keySet().forEach(id -> {
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
        });
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
                throw new IllegalArgumentException("No epic by id %s".formatted(id));
            }
        } else {
            System.out.println("Ошибка: попытка обновить эпик пустым значением");
        }
    }

    @Override
    public void deleteEpicById(Integer id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.remove(id);
            prioritizedTasks.remove(epic);
            List<Integer> subtasksById = epic.getSubtasks();
            subtasksById.forEach(subtaskId -> {
                subtasks.remove(subtaskId);
                prioritizedTasks.remove(subtasks.get(subtaskId));
                historyManager.remove(subtaskId);
            });
            historyManager.remove(id);
        } else {
            throw new IllegalArgumentException("No epic by id %s".formatted(id));
        }
    }

    private Integer getGeneratedId() {
        return ++id;
    }

    @Override
    public List<Subtask> getSubtasksInEpic(List<Integer> subtasksById) {
        return subtasksById.stream().map(subtasks::get).collect(Collectors.toList());
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

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private boolean checkIntersections(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime endTime1 = task1.getEndTime();

        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime endTime2 = task2.getEndTime();

        if (Stream.of(startTime1, endTime1, startTime2, endTime2).filter(Objects::nonNull).count() != 4) {
            return false;
        }

        return startTime1.isBefore(endTime2) && endTime1.isAfter(startTime2);
    }

    private void addTaskInPrioritizedTasks(Task task) {
        if (task.getStartTime() != null && task.getDuration() != null) {
            if (!hasIntersections(task)) {
                prioritizedTasks.add(task);
            }
        }
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasksInEpic = getSubtasksInEpic(epic);

        if (!subtasksInEpic.isEmpty()) {
            LocalDateTime startTime = subtasksInEpic.stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime endTime = subtasksInEpic.stream()
                    .map(Subtask::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            Duration duration = subtasksInEpic.stream()
                    .map(Subtask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, Duration::plus);

            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
        }
    }
}
