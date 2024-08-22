package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.List;

public interface HistoryManager {
    void addTaskInHistory(Task task);

    List<Task> getHistory();

}
