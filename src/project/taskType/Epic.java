package project.taskType;

import project.taskStatus.Status;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {

    private List<Integer> subtasksById = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
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
