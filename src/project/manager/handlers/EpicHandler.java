package project.manager.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import project.manager.TaskManager;
import project.taskType.Epic;
import project.taskType.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
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
                case GET_EPIC:
                    handleGetEpic(exchange, splitStrings);
                    break;
                case GET_EPICS:
                    handleGetAllEpics(exchange);
                    break;
                case GET_EPIC_SUBTASKS:
                    handleGetAllSubtasksInEpic(exchange, splitStrings);
                    break;
                case POST_ADD_EPIC:
                    handleAddEpic(exchange, body);
                    break;
                case POST_UPDATE_EPIC:
                    handleUpdateEpic(exchange, body);
                    break;
                case DELETE_EPIC:
                    handleDeleteEpic(exchange, splitStrings);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGetEpic(HttpExchange exchange, String[] splitStrings) throws IOException {
        if (splitStrings.length < 3) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Integer epicId = Integer.parseInt(splitStrings[2]);
            Epic epic = taskManager.getEpicById(epicId);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendText(exchange, gson.toJson(epics));
    }

    private void handleGetAllSubtasksInEpic(HttpExchange exchange, String[] splitStrings) throws IOException {
        if (splitStrings.length < 3) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Integer epicId = Integer.parseInt(splitStrings[2]);
            Epic epic = taskManager.getEpicById(epicId);
            if (epic != null) {
                List<Subtask> subtasksInEpic = taskManager.getSubtasksInEpic(epic);
                sendText(exchange, gson.toJson(subtasksInEpic));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }

    private void handleAddEpic(HttpExchange exchange, String body) throws IOException {
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            taskManager.addNewEpic(epic);
            sendCreated(exchange, "Эпик добавлен");
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (NullPointerException e) {
            sendNotFound(exchange);
        }
    }

    private void handleUpdateEpic(HttpExchange exchange, String body) throws IOException {
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            if (taskManager.getEpicById(epic.getId()) != null) {
                taskManager.updateEpic(epic);
                sendCreated(exchange, "Эпик обновлен");
            } else {
                sendNotFound(exchange);
            }
        } catch (JsonSyntaxException e) {
            sendBadRequest(exchange);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, String[] splitStrings) throws IOException {
        if (splitStrings.length < 3) {
            sendBadRequest(exchange);
            return;
        }
        try {
            Integer epicId = Integer.parseInt(splitStrings[2]);
            if (taskManager.getEpicById(epicId) != null) {
                taskManager.deleteEpicById(epicId);
                sendText(exchange, "Эпик удален");
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendBadRequest(exchange);
        }
    }
}