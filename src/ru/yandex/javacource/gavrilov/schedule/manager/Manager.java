package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;

public class Manager {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
