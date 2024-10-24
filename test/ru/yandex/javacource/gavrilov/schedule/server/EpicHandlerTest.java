package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.javacource.gavrilov.schedule.adapter.SubtaskListTypeToken;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicHandlerTest {
    TaskManager manager = Manager.getDefault();
    HttpTaskServer server = new HttpTaskServer(manager);
    Gson gson = server.getGson();
    HttpClient client;

    @BeforeEach
    public void startServer() throws IOException {
        manager.removeTasks();
        manager.removeEpics();
        manager.removeSubtasks();
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void stopServer(){
        server.stop();
        client.close();
    }

    @Test
    public void addEpic_shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        Assertions.assertNotNull(epicsFromManager);
        Assertions.assertEquals(1, epicsFromManager.size());
        Assertions.assertEquals("Epic 1", epicsFromManager.getFirst().getName());
    }

    @Test
    public void getEpic_shouldGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        manager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Epic jsonEpic = gson.fromJson(response.body(), Epic.class);
        Assertions.assertEquals(epic, jsonEpic);
    }

    @Test
    public void deleteEpic_shouldRemoveEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1", TaskStatus.NEW);
        manager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        Assertions.assertEquals(1, manager.getEpics().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(0, manager.getEpics().size());
    }

    @Test
    public void getEpic_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteEpic_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void getEpicSubtasks_shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        Subtask subtask = new Subtask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW,1, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30));
        manager.addEpic(epic);
        manager.addSubtask(subtask);

        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        List<Subtask> subs = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        Assertions.assertEquals(subtask, subs.getFirst());
    }
}

