package project.taskType;

import project.taskStatus.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final Integer epicId;

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, Integer epicId,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask { name = " + getName() +
                ", description = " + getDescription() +
                ", status = " + getStatus() +
                ", epicId = " + getEpicId() + " }";
    }
}
