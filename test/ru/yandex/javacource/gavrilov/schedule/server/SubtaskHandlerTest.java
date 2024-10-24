package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtaskHandlerTest {
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
    public void addSubtask_shouldAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        Subtask sub = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW,1, Duration.ofMinutes(5), LocalDateTime.now());
        String subJson = gson.toJson(sub);

        manager.addEpic(epic);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        Assertions.assertNotNull(subtasksFromManager);
        Assertions.assertEquals(1, subtasksFromManager.size());
        Assertions.assertEquals("Sub 1", subtasksFromManager.getFirst().getName());
    }

    @Test
    public void updateSubtask_shouldUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        Integer epicId = manager.addEpic(epic);
        Subtask sub = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW,epicId, Duration.ofMinutes(5), LocalDateTime.now());

        Integer id = manager.addSubtask(sub);

        Subtask subUpd = new Subtask( "UPDATE", "Testing sub 1",
                TaskStatus.NEW,epicId,id, Duration.ofMinutes(5), LocalDateTime.now());
        String subJson = gson.toJson(subUpd);

        URI url = URI.create("http://localhost:8080/subtasks/"+id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        Assertions.assertNotNull(subtasksFromManager);
        Assertions.assertEquals(1, subtasksFromManager.size());
        Assertions.assertEquals("UPDATE", subtasksFromManager.getFirst().getName(), String.valueOf(response.statusCode()));
    }

    @Test
    public void getSubtask_shouldGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        Subtask sub = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW,1, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addEpic(epic);
        manager.addSubtask(sub);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Subtask jsonSub = gson.fromJson(response.body(), Subtask.class);
        Assertions.assertEquals(sub, jsonSub);
    }

    @Test
    public void deleteSubtask_shouldRemoveSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        Subtask sub = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW,1, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addEpic(epic);
        manager.addSubtask(sub);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        Assertions.assertEquals(1, manager.getSubtasks().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(0, manager.getSubtasks().size());
    }

    @Test
    public void getSubtask_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteSubtask_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void addSubtask_shouldNotAddSubtaskIntersection() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        Subtask sub1 = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW,1, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub2 = new Subtask("Sub 2", "Testing sub 2",
                TaskStatus.NEW,1, Duration.ofMinutes(10), LocalDateTime.now());
        manager.addEpic(epic);
        manager.addSubtask(sub1);
        String subJson = gson.toJson(sub2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
    }

    @Test
    public void updateSubtask_shouldNotUpdateSubtaskIntersection() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",TaskStatus.NEW);
        Subtask sub1 = new Subtask("Sub 1", "Testing sub 1",
                TaskStatus.NEW,1, Duration.ofMinutes(5), LocalDateTime.now());
        Subtask sub2 = new Subtask( "UPDATE", "Testing sub 2",
                TaskStatus.NEW,1,2, Duration.ofMinutes(12), LocalDateTime.now().plusMinutes(30));
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(30));

        manager.addEpic(epic);
        manager.addSubtask(sub1);
        manager.addTask(task);
        String subJson = gson.toJson(sub2);

        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode(),response.body());
    }
}
