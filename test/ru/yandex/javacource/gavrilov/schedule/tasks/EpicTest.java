package ru.yandex.javacource.gavrilov.schedule.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

public class EpicTest {
    @Test
    public void shouldEpicsEquallyPerId() {
        TaskManager manager = Manager.getDefault();
        Epic epic = new Epic("epic1", "des1", TaskStatus.NEW);
        int id = manager.addEpic(epic);
        epic.setId(id);
        Assertions.assertEquals(epic, manager.getEpicById(id));
    }
}
