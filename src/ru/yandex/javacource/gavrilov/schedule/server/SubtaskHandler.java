package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.javacource.gavrilov.schedule.adapter.SubtaskListTypeToken;
import ru.yandex.javacource.gavrilov.schedule.exception.TaskValidationException;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.manager.Type;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class SubtaskHandler extends BaseHttpHandler{
    public SubtaskHandler(TaskManager manager, Gson gson) {
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
                String response = gson.toJson(manager.getSubtasks(),new SubtaskListTypeToken().getType());
                writeResponse(exchange,response,200);
                break;
            }
            case GET_ID:{
                Subtask subtask = manager.getSubtaskById(Integer.parseInt(pathParts[2]));
                if (subtask == null){
                    writeResponse(exchange,"Подзадача не найдена",404);
                }else {
                    String response = gson.toJson(subtask);
                    writeResponse(exchange,response,200);
                }
                break;
            }
            case ADD: {
                try {
                    Integer id = manager.addSubtask(checkSubtask(body,exchange));
                    writeResponse(exchange,"Подзадача добавлена. Id позадачи - "+id,201);
                }catch (TaskValidationException e){
                    writeResponse(exchange,"Задачи пересекаются",406);
                }
                break;
            }
            case UPDATE: {
                try {
                    manager.updateSubtask(checkSubtask(body,exchange));
                    writeResponse(exchange,"Подзадача обновлена",201);
                }catch (TaskValidationException e){
                    writeResponse(exchange,"Задачи пересекаются",406);
                }
                break;
            }
            case DELETE:
                Subtask subtask = manager.getSubtaskById(Integer.parseInt(pathParts[2]));
                if (subtask==null) {
                    writeResponse(exchange, "Подзадача не найдена", 404);
                }else {
                    manager.removeSubtask(Integer.parseInt(pathParts[2]));
                    writeResponse(exchange,"Подзадача удалена",200);
                }
                break;
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }


    }

    protected Subtask deserializeSubtask(String str){
        return gson.fromJson(str,Subtask.class);
    }

    private Subtask checkSubtask(String body,HttpExchange exchange) throws IOException {
        Subtask subtask = deserializeSubtask(body);
        Subtask subtask1 = null;
        if (subtask.getType().equals(Type.SUBTASK)){
            subtask1 = subtask;
        }
        if (subtask1==null){
            writeResponse(exchange,"Передана не подзадача", 406);
        }
        return subtask1;
    }
}
