package project.manager.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import project.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import project.taskType.Task;

public class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();
            Endpoint endpoint = getEndpoint(requestPath, requestMethod);
            String[] splitStrings = requestPath.split("/");
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            switch (endpoint) {
                case GET_TASK:
                    handleGetTask(exchange, splitStrings);
                    break;
                case GET_TASKS:
                    handleGetAllTasks(exchange);
                    break;
                case POST_ADD_TASK:
                    handleAddTask(exchange, body);
                    break;
                case POST_UPDATE_TASK:
                    handleUpdateTask(exchange, body);
                    break;
                case DELETE_TASK:
                    handleDeleteTask(exchange, splitStrings);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGetTask(HttpExchange exchange, String[] splitStrings) throws IOException {
        if (splitStrings.length < 3) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Integer taskId = Integer.parseInt(splitStrings[2]);
            Task task = taskManager.getTaskById(taskId);
            if (task != null) {
                sendText(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(exchange, gson.toJson(tasks));
    }

    private void handleAddTask(HttpExchange exchange, String body) throws IOException {
        try {
            Task task = gson.fromJson(body, Task.class);
            taskManager.addNewTask(task);
            sendCreated(exchange, "Задача добавлена");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        }
    }

    private void handleUpdateTask(HttpExchange exchange, String body) throws IOException {
        try {
            Task task = gson.fromJson(body, Task.class);
            if (taskManager.getTaskById(task.getId()) != null) {
                taskManager.updateTask(task);
                sendCreated(exchange, "Задача обновлена");
            } else {
                sendNotFound(exchange);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange, String[] splitStrings) throws IOException {
        if (splitStrings.length < 3) {
            sendBadRequest(exchange);
            return;
        }
        try {
            int taskId = Integer.parseInt(splitStrings[2]);
            if (taskManager.getTaskById(taskId) != null) {
                taskManager.deleteTask(taskId);
                sendText(exchange, "Задача удалена");
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }
}