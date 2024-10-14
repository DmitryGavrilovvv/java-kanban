package ru.yandex.javacource.gavrilov.schedule.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    public void shouldEpicStatusNewIfAllSubtaskStatusNew() {
        Epic epic = new Epic("epic1", "des1", TaskStatus.NEW);
        int id = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("s", "d", TaskStatus.NEW, id, null, null);
        Subtask subtask2 = new Subtask("s", "d", TaskStatus.NEW, id, null, null);
        Subtask subtask3 = new Subtask("s", "d", TaskStatus.NEW, id, null, null);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        Assertions.assertEquals(TaskStatus.NEW, manager.getEpicById(id).getStatus());
    }

    @Test
    public void shouldEpicStatusDoneIfAllSubtaskStatusDone() {
        Epic epic = new Epic("epic1", "des1", TaskStatus.NEW);
        int id = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("s", "d", TaskStatus.DONE, id, null, null);
        Subtask subtask2 = new Subtask("s", "d", TaskStatus.DONE, id, null, null);
        Subtask subtask3 = new Subtask("s", "d", TaskStatus.DONE, id, null, null);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        Assertions.assertEquals(TaskStatus.DONE, manager.getEpicById(id).getStatus());
    }

    @Test
    public void shouldEpicStatusInProgressIfAllSubtaskStatusInProgress() {
        Epic epic = new Epic("epic1", "des1", TaskStatus.NEW);
        int id = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("s", "d", TaskStatus.IN_PROGRESS, id, null, null);
        Subtask subtask2 = new Subtask("s", "d", TaskStatus.IN_PROGRESS, id, null, null);
        Subtask subtask3 = new Subtask("s", "d", TaskStatus.IN_PROGRESS, id, null, null);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(id).getStatus());
    }

    @Test
    public void shouldEpicStatusInProgressIfSubtaskStatusesNewAndDone() {
        Epic epic = new Epic("epic1", "des1", TaskStatus.NEW);
        int id = manager.addEpic(epic);
        Subtask subtask1 = new Subtask("s", "d", TaskStatus.NEW, id, null, null);
        Subtask subtask2 = new Subtask("s", "d", TaskStatus.DONE, id, null, null);
        Subtask subtask3 = new Subtask("s", "d", TaskStatus.DONE, id, null, null);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(id).getStatus());
    }

    @Test
    public void shouldDoNotSaveSubtaskIfNoEpic() {
        Subtask subtask1 = new Subtask("s", "d", TaskStatus.NEW, 2, null, null);
        Integer id = manager.addSubtask(subtask1);//если эпика нету нету то подзадача не добавится и вернёт null
        Assertions.assertNull(id);
    }

    @Test
    public void shouldDoNotSaveTaskInPrioritizedTasksIfIntersect() {
        Task task1 = new Task("n", "d", TaskStatus.NEW, Duration.ofMinutes(10)
                , LocalDateTime.of(2024, 2, 12, 16, 20, 0));
        Task task2 = new Task("n", "d", TaskStatus.NEW, Duration.ofMinutes(20)
                , LocalDateTime.of(2024, 2, 12, 16, 30, 0));
        Task task3 = new Task("n", "d", TaskStatus.NEW, Duration.ofMinutes(30)
                , LocalDateTime.of(2024, 2, 12, 16, 10, 0));
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addTask(task3);
        List<Task> prTasks = manager.getPrioritizedTasks();
        Assertions.assertEquals(prTasks.size(), 2);
    }
}
