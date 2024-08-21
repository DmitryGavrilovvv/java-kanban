package ru.yandex.javacource.gavrilov.schedule.managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.javacource.gavrilov.schedule.manager.HistoryManager;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;


public class ManagerTest {
    @Test
    public void shouldReturnTaskManager() {
        TaskManager manager = Manager.getDefault();
        Assertions.assertNotNull(manager);
    }

    @Test
    public void shouldReturnHistoryManager() {
        HistoryManager manager = Manager.getDefaultHistoryManager();
        Assertions.assertNotNull(manager);
    }
}
