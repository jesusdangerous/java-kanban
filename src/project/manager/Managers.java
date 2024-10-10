package project.manager;

import java.io.File;

public class Managers {
    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultFileBacked(File file) {
        return new FileBackedTaskManager(file);
    }
}
