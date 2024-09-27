package ru.yandex.javacource.gavrilov.schedule.managers;

import org.junit.jupiter.api.*;
import ru.yandex.javacource.gavrilov.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager manager;
    File file;

    @BeforeEach
    public void initTaskManager() throws IOException {
        file = File.createTempFile("data", ".csv", new File("C:\\Users\\Дима\\IdeaProjects\\java-kanban\\src\\ru\\yandex\\javacource\\gavrilov\\schedule\\manager\\"));
        manager = FileBackedTaskManager.loadFromFile(file);
    }

    @Test
    public void shouldFileBackedManagerSaveAndLoadEmptyFile() throws IOException {
        manager.save();
        String str = Files.readString(file.toPath());
        Assertions.assertEquals(str, "id;type;name;status;description;epic\n");
    }

    @Test
    public void shouldFileBackedManagerSaveTasksInFile() throws IOException {
        manager.addTask(new Task("task", "des", TaskStatus.NEW));
        Integer epicid = manager.addEpic(new Epic("task", "des", TaskStatus.NEW));
        manager.addSubtask(new Subtask("task", "des", TaskStatus.NEW, epicid));
        String str = Files.readString(file.toPath());
        Assertions.assertNotNull(str);
    }

    @Test
    public void shouldFileBackedManagerLoadTasksInMemory() {
        Assertions.assertTrue(manager.getTasks().isEmpty() || manager.getEpics().isEmpty() || manager.getSubtasks().isEmpty());
    }

    @AfterEach
    public void deleteFile() throws IOException {
        Files.delete(file.toPath());
    }
}
