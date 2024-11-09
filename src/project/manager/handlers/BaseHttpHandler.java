package project.manager.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import project.manager.Managers;
import project.manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected Gson gson;
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = Managers.getGson();
    }

    protected void sendText(HttpExchange exchange, String text) {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            handleError(exchange, "Произошла ошибка при отправке данных");
        } finally {
            exchange.close();
        }
    }

    protected void sendCreated(HttpExchange exchange, String text) {
        try (exchange) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            handleError(exchange, "Произошла ошибка при создании ресурса");
        } finally {
            exchange.close();
        }
    }

    protected void sendNotFound(HttpExchange exchange) {
        try (exchange) {
            String text = "Объект не был найден";
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(404, 0);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            handleError(exchange, "Произошла ошибка при обработке запроса");
        } finally {
            exchange.close();
        }
    }

    protected void sendHasInteractions(HttpExchange exchange) {
        try (exchange) {
            String text = "Задача пересекается с уже существующими";
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(406, 0);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            handleError(exchange, "Произошла ошибка при обработке запроса");
        } finally {
            exchange.close();
        }
    }

    protected void sendInternalServerError(HttpExchange exchange) {
        try (exchange) {
            String text = "Произошла ошибка при обработке запроса";
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            handleError(exchange, "Произошла ошибка при обработке запроса");
        } finally {
            exchange.close();
        }
    }

    protected void sendBadRequest(HttpExchange exchange) {
        try (exchange) {
            String text = "Некорректный запрос";
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            handleError(exchange, "Произошла ошибка при обработке запроса");
        } finally {
            exchange.close();
        }
    }

    protected void sendMethodNotAllowed(HttpExchange exchange) {
        try (exchange) {
            String text = "Эндпоинт не существует";
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(405, 0);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            handleError(exchange, "Произошла ошибка при обработке запроса");
        } finally {
            exchange.close();
        }
    }

    private void handleError(HttpExchange exchange, String errorMessage) {
        try (exchange) {
            byte[] resp = errorMessage.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(500, resp.length);
            exchange.getResponseBody().write(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] splitStrings = requestPath.split("/");
        switch (requestMethod) {
            case "GET" -> {
                switch (splitStrings[1]) {
                    case "tasks":
                        if (splitStrings.length == 3) {
                            return Endpoint.GET_TASK;
                        } else {
                            return Endpoint.GET_TASKS;
                        }
                    case "subtasks":
                        if (splitStrings.length == 3) {
                            return Endpoint.GET_SUBTASK;
                        } else {
                            return Endpoint.GET_SUBTASKS;
                        }
                    case "epics":
                        if (splitStrings.length == 3) {
                            return Endpoint.GET_EPIC;
                        } else if (splitStrings.length == 4 && "subtasks".equals(splitStrings[3])) {
                            return Endpoint.GET_EPIC_SUBTASKS;
                        } else {
                            return Endpoint.GET_EPICS;
                        }
                    case "history":
                        if (splitStrings.length == 2) {
                            return Endpoint.GET_HISTORY;
                        } else {
                            return Endpoint.UNKNOWN;
                        }
                    case "prioritized":
                        if (splitStrings.length == 2) {
                            return Endpoint.GET_PRIORITIZED_TASKS;
                        } else {
                            return Endpoint.UNKNOWN;
                        }
                    default:
                        return Endpoint.UNKNOWN;
                }
            }
            case "POST" -> {
                switch (splitStrings[1]) {
                    case "tasks":
                        if (splitStrings.length == 3) {
                            return Endpoint.POST_UPDATE_TASK;
                        } else {
                            return Endpoint.POST_ADD_TASK;
                        }
                    case "subtasks":
                        if (splitStrings.length == 3) {
                            return Endpoint.POST_UPDATE_SUBTASK;
                        } else {
                            return Endpoint.POST_ADD_SUBTASK;
                        }
                    case "epics":
                        if (splitStrings.length == 3) {
                            return Endpoint.POST_UPDATE_EPIC;
                        } else {
                            return Endpoint.POST_ADD_EPIC;
                        }
                    default:
                        return Endpoint.UNKNOWN;
                }
            }
            case "DELETE" -> {
                return switch (splitStrings[1]) {
                    case "tasks" -> Endpoint.DELETE_TASK;
                    case "subtasks" -> Endpoint.DELETE_SUBTASK;
                    case "epics" -> Endpoint.DELETE_EPIC;
                    default -> Endpoint.UNKNOWN;
                };
            }
        }

        return Endpoint.UNKNOWN;
    }
}