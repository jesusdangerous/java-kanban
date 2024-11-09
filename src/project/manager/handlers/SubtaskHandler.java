package project.manager.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import project.exceptions.TaskTimeConflictException;
import project.manager.TaskManager;
import project.taskType.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
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
                case GET_SUBTASK:
                    handleGetSubtask(exchange, splitStrings);
                    break;
                case GET_SUBTASKS:
                    handleGetAllSubtasks(exchange);
                    break;
                case POST_ADD_SUBTASK:
                    handleAddSubtask(exchange, body);
                    break;
                case POST_UPDATE_SUBTASK:
                    handleUpdateSubtask(exchange, body);
                    break;
                case DELETE_SUBTASK:
                    handleDeleteSubtask(exchange, splitStrings);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGetSubtask(HttpExchange exchange, String[] splitStrings) throws IOException {
        if (splitStrings.length < 3) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Integer subtaskId = Integer.parseInt(splitStrings[2]);
            Subtask subtask = taskManager.getSubtaskById(subtaskId);
            if (subtask != null) {
                sendText(exchange, gson.toJson(subtask));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendText(exchange, gson.toJson(subtasks));
    }

    private void handleAddSubtask(HttpExchange exchange, String body) throws IOException {
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            taskManager.addNewSubtask(subtask);
            sendCreated(exchange, "Подзадача добавлена");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange, String body) throws IOException {
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            taskManager.updateSubtask(subtask);
            sendCreated(exchange, "Подзача обновлена");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange);
        } catch (TaskTimeConflictException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange, String[] splitStrings) throws IOException {
        if (splitStrings.length < 3) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Integer subtaskId = Integer.parseInt(splitStrings[2]);
                taskManager.deleteSubtaskById(subtaskId);
                sendText(exchange, "Подзача удалена");
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange);
        }
    }
}