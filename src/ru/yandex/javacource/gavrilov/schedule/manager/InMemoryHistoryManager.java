package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() >= 10) {
            history.removeFirst();
        }
        Task addTask = new Task(task.getName(), task.getDescription(), task.getStatus(), task.getId());
        history.add(addTask);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
