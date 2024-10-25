package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.gavrilov.schedule.adapter.EpicListTypeToken;
import ru.yandex.javacource.gavrilov.schedule.adapter.SubtaskListTypeToken;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.manager.Type;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String body;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            body = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        switch (endpoint) {
            case GET: {
                String response = gson.toJson(manager.getEpics(), new EpicListTypeToken().getType());
                writeResponse(exchange, response, 200);
                break;
            }
            case GET_ID: {
                Epic epic = manager.getEpicById(Integer.parseInt(pathParts[2]));
                if (epic == null) {
                    writeResponse(exchange, "Эпик не найден", 404);
                } else {
                    serializeEpic(epic, exchange);
                }
                break;
            }
            case ADD: {
                Integer id = manager.addEpic(checkEpic(body, exchange));
                writeResponse(exchange, "Эпик добавлен. Id эпика - " + id, 201);
                break;
            }
            case GET_EPIC_SUBTASKS: {
                String response = gson.toJson(manager.getAllEpicSubtasks(Integer.parseInt(pathParts[2])), new SubtaskListTypeToken().getType());
                writeResponse(exchange, response, 200);
                break;
            }
            case DELETE:
                Epic epic = manager.getEpicById(Integer.parseInt(pathParts[2]));
                if (epic == null) {
                    writeResponse(exchange, "Эпик не найдена", 404);
                } else {
                    manager.removeEpic(Integer.parseInt(pathParts[2]));
                    writeResponse(exchange, "Эпик удален", 200);
                }
                break;
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void serializeEpic(Epic epic, HttpExchange exchange) throws IOException {
        String response = gson.toJson(epic);
        writeResponse(exchange, response, 200);
    }

    protected Task deserializeEpic(String str) {
        return gson.fromJson(str, Epic.class);
    }

    private Epic checkEpic(String body, HttpExchange exchange) throws IOException {
        Task task = deserializeEpic(body);
        Epic epic = null;
        if (task.getType().equals(Type.EPIC)) {
            epic = (Epic) task;
        }
        if (epic == null) {
            writeResponse(exchange, "Передан не Эпик", 406);
        }
        return epic;
    }
}
