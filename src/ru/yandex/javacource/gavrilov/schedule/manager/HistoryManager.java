package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.List;

public interface HistoryManager {
    void addTaskInHistory(Task task);

    void remove(int id);

    void clear();

    List<Task> getHistory();

}
