package project.manager;

import com.sun.net.httpserver.HttpServer;
import project.manager.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        createContexts(taskManager);
    }

    private void createContexts(TaskManager taskManager) {
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void startServer() {
        httpServer.start();
        System.out.println("Server started");
    }

    public void stopServer() {
        httpServer.stop(0);
        System.out.println("Server stopped");
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new InMemoryTaskManager();

        HttpTaskServer httpServer = new HttpTaskServer(taskManager);

        httpServer.startServer();
        System.out.println("Server launched on port " + PORT);
    }
}
