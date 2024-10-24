package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.gavrilov.schedule.adapter.TaskListTypeToken;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        if (endpoint.equals(Endpoint.GET)) {
            String response = gson.toJson(manager.getHistory(), new TaskListTypeToken().getType());
            writeResponse(exchange, response, 200);
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
