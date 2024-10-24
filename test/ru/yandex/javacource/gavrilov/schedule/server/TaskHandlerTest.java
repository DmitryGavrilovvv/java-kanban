package ru.yandex.javacource.gavrilov.schedule.server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
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

public class TaskHandlerTest {
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
    public void addTask_shouldAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode());
        List<Task> tasksFromManager = manager.getTasks();
        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("Test 2", tasksFromManager.getFirst().getName());
    }

    @Test
    public void getTask_shouldGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);

        String taskJson = gson.toJson(task);
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(taskJson,response.body());
    }

    @Test
    public void updateTask_shouldUpdateTask() throws IOException, InterruptedException {

        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);

        Task updTask = new Task("t1","d1",
                TaskStatus.NEW,1, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(updTask);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response.statusCode(),response.body());

        List<Task> tasksFromManager = manager.getTasks();

        Assertions.assertNotNull(tasksFromManager);
        Assertions.assertEquals(1, tasksFromManager.size());
        Assertions.assertEquals("t1", tasksFromManager.getFirst().getName());
    }

    @Test
    public void deleteTask_shouldRemoveTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.addTask(task);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        Assertions.assertEquals(1, manager.getTasks().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void getTask_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteTask_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode());
    }

    @Test
    public void addTask_shouldNotAddTaskIntersection() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(20), LocalDateTime.now());
        manager.addTask(task1);
        String taskJson = gson.toJson(task2);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode(),response.body());
    }

    @Test
    public void updateTask_shouldNotUpdateTaskIntersection() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30));
        Task updTask = new Task( "UPDATE", "Testing task 2",
                TaskStatus.NEW,1, Duration.ofMinutes(40), LocalDateTime.now());

        manager.addTask(task1);
        manager.addTask(task2);
        String taskJson = gson.toJson(updTask);

        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, response.statusCode());
    }
}
