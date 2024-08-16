package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.ArrayList;

public interface TaskManager {
    Integer addTask(Task task);

    void updateTask(Task task);

    Integer addEpic(Task task);

    void updateEpic(Epic epic);

    Integer addSubtask(Task task);

    void updateSubtask(Subtask subtask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    void removeTasks();

    void removeEpics();

    void removeSubtasks();

    void removeAllTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    Subtask getSubtaskById(int id);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<Subtask> getSubtasks();

    ArrayList<Subtask> getAllEpicSubtasks(int epicId);

    HistoryManager getHistoryManager();
}
