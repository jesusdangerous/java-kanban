package project.taskType;

import project.taskStatus.Status;

public class Subtask extends Task {

    private final Integer epicId;

    public Subtask(String name, String description, Status status, Integer epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask { name = " + getName() +
                ", description = " + getDescription() +
                ", status = " + getStatus() +
                ", epicId = " + getEpicId() + " }";
    }
}
