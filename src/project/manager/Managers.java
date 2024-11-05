package project.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import project.manager.adapters.DurationAdapter;
import project.manager.adapters.LocalDateTimeAdapter;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

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

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
