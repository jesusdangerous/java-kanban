package project.taskType;

import project.taskStatus.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {

    private List<Integer> subtasksById = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
    }

    public List<Integer> getSubtasks() {
        return subtasksById;
    }

    public void setSubtasks(List<Integer> subtasksById) {
        this.subtasksById = subtasksById;
    }

    public void addSubtask(Integer subtaskId) {
        subtasksById.add(subtaskId);
    }

    public void deleteSubtask(Integer subtaskId) {
        subtasksById.remove(subtaskId);
    }

    public void deleteAllSubtasks() {
        subtasksById.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic { name = " + getName() +
                ", description = " + getDescription() +
                ", status = " + getStatus() +
                ", id = " + getId() +
                ", subtaskById = " + getSubtasks() + " }";
    }
}
