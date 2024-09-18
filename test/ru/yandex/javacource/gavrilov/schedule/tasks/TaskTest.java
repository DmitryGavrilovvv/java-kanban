package ru.yandex.javacource.gavrilov.schedule.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

public class TaskTest {

    @Test
    public void shouldTasksEquallyPerId() {
        TaskManager manager = Manager.getDefault();
        Task task = new Task("name1", "des1", TaskStatus.NEW);
        int id = manager.addTask(task);
        task.setId(id);
        Assertions.assertEquals(task, manager.getTaskById(id));
    }


}
