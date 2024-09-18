package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.List;

public interface TaskManager {
    Integer addTask(Task task);

    void updateTask(Task task);

    Integer addEpic(Epic epic);

    void updateEpic(Epic epic);

    Integer addSubtask(Subtask subtask);

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

    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    List<Subtask> getAllEpicSubtasks(int epicId);

    List<Task> getHistoryManager();
}
