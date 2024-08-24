package project.manager;

import project.taskType.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> history = new ArrayList<>();
    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Ошибка: попытка добавить пустую задачу");
        } else {
            if (history.size() > 10) {
                history.removeFirst();
            }
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
