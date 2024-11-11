package project.manager.handlers;

import com.sun.net.httpserver.HttpExchange;
import project.manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();
            Endpoint endpoint = getEndpoint(requestPath, requestMethod);

            if (!"GET".equalsIgnoreCase(requestMethod)) {
                sendMethodNotAllowed(exchange);
                return;
            }

            if (endpoint.equals(Endpoint.GET_PRIORITIZED_TASKS)) {
                sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }
}
