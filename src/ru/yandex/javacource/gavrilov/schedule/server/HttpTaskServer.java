package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.gavrilov.schedule.adapter.DurationAdapter;
import ru.yandex.javacource.gavrilov.schedule.adapter.LocalDateTimeAdapter;
import ru.yandex.javacource.gavrilov.schedule.adapter.StatusAdapter;
import ru.yandex.javacource.gavrilov.schedule.adapter.TypeTaskAdapter;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.manager.Type;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private HttpServer httpServer;
    private final TaskManager manager;
    public static final int PORT = 8080;
    private final GsonBuilder gsonBuilder = new GsonBuilder();
    private final Gson gson = gsonBuilder.setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Type.class, new TypeTaskAdapter())
            .registerTypeAdapter(TaskStatus.class, new StatusAdapter())
            .create();

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer(Manager.getDefault());
        server.manager.addTask(new Task("t", "d", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now()));
        Integer id = server.manager.addEpic(new Epic("t", "d", TaskStatus.NEW));
        server.manager.addSubtask(new Subtask("t", "d", TaskStatus.NEW, id, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10)));
        server.start();
    }

    private void createContexts() {
        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        httpServer.createContext("/epics", new EpicHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
        httpServer.createContext("/history", new HistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        createContexts();
        httpServer.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
        System.out.println("Сервер остановлен.");
    }
}
