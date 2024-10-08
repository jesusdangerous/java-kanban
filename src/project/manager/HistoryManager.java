package project.manager;

import project.taskType.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(Integer id);
}
