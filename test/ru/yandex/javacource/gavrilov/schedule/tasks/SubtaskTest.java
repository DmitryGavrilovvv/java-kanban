package ru.yandex.javacource.gavrilov.schedule.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

public class SubtaskTest {
    @Test
    public void shouldTasksEquallyPerId() {
        TaskManager manager = Manager.getDefault();
        Epic epic = new Epic("epic1", "des1", TaskStatus.NEW);
        int epicId = manager.addEpic(epic);
        Subtask subtask = new Subtask("name1", "des1", TaskStatus.NEW, epicId);
        int id = manager.addSubtask(subtask);
        subtask.setId(id);
        Assertions.assertEquals(subtask, manager.getSubtaskById(id));
    }

    @Test
    public void shouldSubtaskCannotAddedSubtask() {
        TaskManager manager = Manager.getDefault();
        Subtask subtask = new Subtask("epic1", "des1", TaskStatus.NEW, 1);
        //Assertions.assertNull(manager.addEpic(subtask));
    }
}
