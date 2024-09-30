package ru.yandex.javacource.gavrilov.schedule.manager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


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
