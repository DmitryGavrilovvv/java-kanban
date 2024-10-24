package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler{
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager manager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager manager,Gson gson){
        this.manager=manager;
        this.gson=gson;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }
    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod){
            case "GET": {
                if (pathParts.length == 3){
                    return Endpoint.GET_ID;
                }else if(pathParts.length == 4 && pathParts[3].equals("subtasks")){
                    return Endpoint.GET_EPIC_SUBTASKS;
                }else{
                    return Endpoint.GET;
                }
            }
            case "POST":{
                if (pathParts.length == 3){
                    return Endpoint.UPDATE;
                }else {
                    return Endpoint.ADD;
                }
            }
            case "DELETE":{
                return Endpoint.DELETE;
            }
        }
        return Endpoint.UNKNOWN;
    }

    protected void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }
}
