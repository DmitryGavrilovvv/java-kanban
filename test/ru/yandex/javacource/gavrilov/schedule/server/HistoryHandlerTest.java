package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.javacource.gavrilov.schedule.adapter.TaskListTypeToken;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import org.junit.jupiter.api.Assertions;
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

public class HistoryHandlerTest {
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
    public void handle_shouldReturnHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10));
        Task task3 = new Task("Test 3", "Testing task 3",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(20));
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(3);

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(3, tasks.size());
        Assertions.assertEquals("Test 1", tasks.get(0).getName());
        Assertions.assertEquals("Test 2", tasks.get(1).getName());
        Assertions.assertEquals("Test 3", tasks.get(2).getName());
    }

    @Test
    public void handle_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/histor");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }
}

