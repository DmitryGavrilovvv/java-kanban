package ru.yandex.javacource.gavrilov.schedule.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.util.List;

public class InMemoryHistoryManagerTest {
    TaskManager manager;

    @BeforeEach
    public void initTaskManager() {
        manager = Manager.getDefault();
    }

    @Test
    public void shouldHistoryManagerSaveTaskAndUpdateTask() {
        Task task1 = new Task("task", "desc", TaskStatus.NEW, 1, null, null);
        Task task2 = new Task("task1", "desc", TaskStatus.NEW, 1, null, null);
        manager.addTask(task1);
        Task task = manager.getTaskById(1);
        manager.updateTask(task2);
        task = manager.getTaskById(1);
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(task2, history.getFirst());
    }

    @Test
    public void shouldLinkedListAddCorrect() {
        Task task = new Task("task", "desc", TaskStatus.NEW, null, null);
        Task task2 = new Task("task2", "desc", TaskStatus.NEW, null, null);
        Integer id1 = manager.addTask(task);
        Integer id2 = manager.addTask(task2);
        manager.getTaskById(id1);
        manager.getTaskById(id2);
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(history.get(1), task2);
    }

    @Test
    public void shouldLinkedListRemoveCorrect() {
        Task task = new Task("task", "desc", TaskStatus.NEW, null, null);
        Task task2 = new Task("task2", "desc", TaskStatus.NEW, null, null);
        Task task3 = new Task("task3", "desc", TaskStatus.NEW, null, null);
        Integer id1 = manager.addTask(task);
        Integer id2 = manager.addTask(task2);
        Integer id3 = manager.addTask(task3);
        manager.getTaskById(id1);
        manager.getTaskById(id2);
        manager.getTaskById(id3);
        manager.removeTask(id2);
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(history.get(1), task3);
    }

    @Test
    public void shouldClearHistory() {
        Task task = new Task("task", "desc", TaskStatus.NEW, null, null);
        Task task2 = new Task("task2", "desc", TaskStatus.NEW, null, null);
        Task task3 = new Task("task3", "desc", TaskStatus.NEW, null, null);
        Integer id1 = manager.addTask(task);
        Integer id2 = manager.addTask(task2);
        Integer id3 = manager.addTask(task3);
        manager.getTaskById(id1);
        manager.getTaskById(id2);
        manager.getTaskById(id3);
        manager.clearHistory();
        Assertions.assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldCorrectReturnHistory() {
        Task task = new Task("task", "desc", TaskStatus.NEW, null, null);
        Integer id1 = manager.addTask(task);
        manager.getTaskById(id1);
        Assertions.assertTrue(manager.getHistory().contains(manager.getTaskById(id1)));
    }

    @Test
    public void shouldCorrectRemoveFirstTaskInHistory() {
        Task task = new Task("task", "desc", TaskStatus.NEW, null, null);
        Task task2 = new Task("task2", "desc", TaskStatus.NEW, null, null);
        Task task3 = new Task("task3", "desc", TaskStatus.NEW, null, null);
        Integer id1 = manager.addTask(task);
        Integer id2 = manager.addTask(task2);
        Integer id3 = manager.addTask(task3);
        manager.getTaskById(id1);
        manager.getTaskById(id2);
        manager.getTaskById(id3);
        manager.removeTask(id1);
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(history.getFirst(), manager.getTaskById(id2));
    }

    @Test
    public void shouldCorrectRemoveLastTaskInHistory() {
        Task task = new Task("task", "desc", TaskStatus.NEW, null, null);
        Task task2 = new Task("task2", "desc", TaskStatus.NEW, null, null);
        Task task3 = new Task("task3", "desc", TaskStatus.NEW, null, null);
        Integer id1 = manager.addTask(task);
        Integer id2 = manager.addTask(task2);
        Integer id3 = manager.addTask(task3);
        manager.getTaskById(id1);
        manager.getTaskById(id2);
        manager.getTaskById(id3);
        manager.removeTask(id3);
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(history.getLast(), manager.getTaskById(id2));
    }

    @Test
    public void shouldCorrectRemoveMiddleTaskInHistory() {
        Task task = new Task("task", "desc", TaskStatus.NEW, null, null);
        Task task2 = new Task("task2", "desc", TaskStatus.NEW, null, null);
        Task task3 = new Task("task3", "desc", TaskStatus.NEW, null, null);
        Integer id1 = manager.addTask(task);
        Integer id2 = manager.addTask(task2);
        Integer id3 = manager.addTask(task3);
        manager.getTaskById(id1);
        manager.getTaskById(id2);
        manager.getTaskById(id3);
        manager.removeTask(id2);
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(history.get(1), manager.getTaskById(id3));
    }
}
