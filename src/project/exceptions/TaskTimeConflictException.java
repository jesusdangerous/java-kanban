package project.exceptions;

public class TaskTimeConflictException extends RuntimeException {
    public TaskTimeConflictException(String message) {
        super(message);
    }
}
