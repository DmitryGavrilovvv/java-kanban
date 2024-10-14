package ru.yandex.javacource.gavrilov.schedule.manager;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @BeforeEach
    public void initTaskManager() throws IOException {
        manager = createTaskManager();
    }

    protected abstract T createTaskManager() throws IOException;

    @Test
    public void shouldAddAndSearchTask() {
        Task task = new Task("task", "desc", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        int id = manager.addTask(task);
        Assertions.assertNotNull(manager.getTaskById(id));
    }

    @Test
    public void shouldAddAndSearchEpic() {
        Epic epic = new Epic("task", "desc", TaskStatus.NEW);
        int id = manager.addEpic(epic);
        Assertions.assertNotNull(manager.getEpicById(id));
    }

    @Test
    public void shouldAddAndSearchSubtask() {
        Epic epic = new Epic("task", "desc", TaskStatus.NEW);
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("task", "desc", TaskStatus.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        int id = manager.addSubtask(subtask);
        Assertions.assertNotNull(manager.getSubtaskById(id));
    }

    @Test
    public void shouldNotConflictTasksPerId() {
        Task task1 = new Task("task", "desc", TaskStatus.NEW, 1, Duration.ofMinutes(1), LocalDateTime.now());
        Task task2 = new Task("task", "desc", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        manager.addTask(task2);//при добавлении в мапу должен присвоиться id = 1
        Integer id2 = manager.addTask(task1);
        Assertions.assertEquals(task1.getId(), id2);
    }

    @Test
    public void shouldTestNotChangeAddManager() {
        Task task1 = new Task("task", "desc", TaskStatus.NEW, 1, Duration.ofMinutes(1), LocalDateTime.now());
        manager.addTask(task1);
        Task task2 = manager.getTaskById(1);
        Assertions.assertEquals(task1, task2);
    }

    @Test
    public void shouldRemovedTaskDoNotSaveOldId() {
        Task task1 = new Task("task", "desc", TaskStatus.NEW, 1, Duration.ofMinutes(1), LocalDateTime.now());
        int id = manager.addTask(task1);
        manager.removeTask(id);
        Assertions.assertNull(task1.getId());
    }

    @Test
    public void shouldEpicDoNotSaveIdRemovableSubtask() {
        Epic epic = new Epic("epic", "desc", TaskStatus.NEW);
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("subtask", "desc", TaskStatus.NEW, epicId, Duration.ofMinutes(1), LocalDateTime.now());
        int subtaskId = manager.addSubtask(subtask);
        manager.removeSubtask(subtaskId);
        List<Integer> ids = manager.getEpicById(epicId).getSubtasksIds();
        Assertions.assertFalse(ids.contains(subtaskId));
    }

    @Test
    public void shouldChangingFieldInTaskChangeFieldInManager() {
        Task task1 = new Task("task", "desc", TaskStatus.NEW, 1, Duration.ofMinutes(1), LocalDateTime.now());
        int id = manager.addTask(task1);
        task1.setName("test1");
        Assertions.assertEquals(task1.getName(), manager.getTaskById(id).getName());
    }
}
