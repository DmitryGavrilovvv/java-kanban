package ru.yandex.javacource.gavrilov.schedule.manager;


public class Manager {
    private Manager() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
