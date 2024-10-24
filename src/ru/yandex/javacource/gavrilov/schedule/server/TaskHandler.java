package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.gavrilov.schedule.adapter.TaskListTypeToken;
import ru.yandex.javacource.gavrilov.schedule.exception.TaskValidationException;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.manager.Type;
import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class TaskHandler extends BaseHttpHandler{

    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager,gson);
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
                String response = gson.toJson(manager.getTasks(),new TaskListTypeToken().getType());
                writeResponse(exchange,response,200);
                break;
            }
            case GET_ID:{
                    Task task = manager.getTaskById(Integer.parseInt(pathParts[2]));
                    if (task == null) {
                        writeResponse(exchange,"Задача не найдена", 404);
                    } else {
                        String response = gson.toJson(task);
                        writeResponse(exchange, response, 200);
                    }
                break;
            }
            case ADD: {
                try {
                    Integer id = manager.addTask(checkTask(body,exchange));
                    writeResponse(exchange,"Задача добавлена. Id задачи - "+id,201);
                }catch (TaskValidationException e){
                    writeResponse(exchange,"Задачи пересекаются",406);
                }
                break;
            }
            case UPDATE: {
                try {
                    manager.updateTask(checkTask(body,exchange));
                    writeResponse(exchange,"Задача обновлена",201);
                }catch (TaskValidationException e){
                    writeResponse(exchange,"Задачи пересекаются",406);
                }
                break;
            }
            case DELETE:
                Task task = manager.getTaskById(Integer.parseInt(pathParts[2]));
                if (task==null){
                    writeResponse(exchange,"Задача не найдена", 404);
                }else {
                    manager.removeTask(Integer.parseInt(pathParts[2]));
                    writeResponse(exchange,"Задача удалена",200);
                }
                break;
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
    protected Task deserializeTask(String str){
        return gson.fromJson(str,Task.class);
    }

    private Task checkTask(String body, HttpExchange exchange) throws IOException {
        Task task = deserializeTask(body);
        if (task.getType().equals(Type.TASK)){
            return task;
        }else {
            writeResponse(exchange,"Передана не задача", 406);
        }
        return null;
    }
}
