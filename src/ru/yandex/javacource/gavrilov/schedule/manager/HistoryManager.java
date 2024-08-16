package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    public void add(Task task);

    public ArrayList<Task> getHistory();

}
