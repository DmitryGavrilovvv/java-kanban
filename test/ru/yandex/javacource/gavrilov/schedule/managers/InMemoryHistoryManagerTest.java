package ru.yandex.javacource.gavrilov.schedule.managers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
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
    public void shouldLinkedListAddCorrect(){
        Task task = new Task("task", "desc", TaskStatus.NEW);
        Task task2 = new Task("task2", "desc", TaskStatus.NEW);
        Integer id1 = manager.addTask(task);
        Integer id2 = manager.addTask(task2);
        manager.getTaskById(id1);
        manager.getTaskById(id2);
        List<Task> history = manager.getHistoryManager();
        Assertions.assertEquals(history.get(1),task2);
    }

    @Test
    public void shouldLinkedListRemoveCorrect(){
        Task task = new Task("task", "desc", TaskStatus.NEW);
        Task task2 = new Task("task2", "desc", TaskStatus.NEW);
        Task task3 = new Task("task3", "desc", TaskStatus.NEW);
        Integer id1 = manager.addTask(task);
        Integer id2 = manager.addTask(task2);
        Integer id3 = manager.addTask(task3);
        manager.getTaskById(id1);
        manager.getTaskById(id2);
        manager.getTaskById(id3);
        manager.removeTask(id2);
        List<Task> history = manager.getHistoryManager();
        Assertions.assertEquals(history.get(1),task3);
    }
}
