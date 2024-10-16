package ru.yandex.javacource.gavrilov.schedule.manager;

import org.junit.jupiter.api.*;
import ru.yandex.javacource.gavrilov.schedule.exception.ManagerSaveException;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;

    @Override
    protected FileBackedTaskManager createTaskManager() throws IOException {
        file = File.createTempFile("data", ".csv");
        return FileBackedTaskManager.loadFromFile(file);
    }

    @Test
    public void shouldFileBackedManagerSaveAndLoadEmptyFile() throws IOException {
        manager.save();
        String str = Files.readString(file.toPath());
        Assertions.assertEquals(str, "id;type;name;status;description;epic\n");
    }

    @Test
    public void shouldFileBackedManagerSaveTasksInFile() throws IOException {
        manager.addTask(new Task("task", "des", TaskStatus.NEW, null, null));
        Integer epicId = manager.addEpic(new Epic("task", "des", TaskStatus.NEW));
        manager.addSubtask(new Subtask("task", "des", TaskStatus.NEW, epicId, null, null));
        String str = Files.readString(file.toPath());
        Assertions.assertNotNull(str);
    }

    @Test
    public void shouldFileBackedManagerLoadTasksInMemory() {
        Assertions.assertTrue(manager.getTasks().isEmpty() || manager.getEpics().isEmpty() || manager.getSubtasks().isEmpty());
    }

    @Test
    public void testException() {
        manager = new FileBackedTaskManager(new File("path/to/non_existent_file.txt"));
        Assertions.assertThrows(ManagerSaveException.class, () -> manager.addTask(new Task("task", "des", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now())));
    }

    @AfterEach
    public void deleteFile() throws IOException {
        Files.delete(file.toPath());
    }
}
