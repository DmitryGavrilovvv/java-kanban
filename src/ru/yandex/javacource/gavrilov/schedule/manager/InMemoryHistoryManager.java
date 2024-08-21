package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int MAX_SIZE = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public void addTaskInHistory(Task task){
        if (task == null) {
            return;
        }
        if (history.size() >= MAX_SIZE) {
            history.removeFirst();
        }
        history.add(task);
    }
    @Override
    public void addEpicInHistory(Epic epic) {
        if (epic == null) {
            return;
        }
        if (history.size() >= MAX_SIZE) {
            history.removeFirst();
        }
        history.add(epic);
    }
    @Override
    public void addSubtaskInHistory(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (history.size() >= MAX_SIZE) {
            history.removeFirst();
        }
       history.add(subtask);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
