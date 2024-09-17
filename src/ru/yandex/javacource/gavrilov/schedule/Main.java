package ru.yandex.javacource.gavrilov.schedule;

import ru.yandex.javacource.gavrilov.schedule.manager.HistoryManager;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.task.*;

import java.util.List;

public class Main {
    static TaskManager manager = Manager.getDefault();

    public static void main(String[] args) {
        Task task1 = new Task("Помыть посуду", "Вымыть всю посуду дочиста", TaskStatus.NEW);
        Task task2 = new Task("Вытереть пыль", "Вытереть всю пыль в квартире", TaskStatus.NEW);
        int task1Id = manager.addTask(task1);
        manager.addTask(task2);

        Epic epic1 = new Epic("Очистить пол", "Подмести и помыть пол", TaskStatus.NEW);
        int epicId = manager.addEpic(epic1);
        int epic1Id = epicId;
        Subtask subtask1Epic1 = new Subtask("Подмести пол"
                , "Подмести весь пол веником", TaskStatus.NEW, epicId);
        int subtask1Epic1Id = manager.addSubtask(subtask1Epic1);

        Subtask subtask2Epic1 = new Subtask("Помыть пол"
                , "Помыть весь пол тряпкой", TaskStatus.NEW, epicId);
        int subtask2Epic1Id = manager.addSubtask(subtask2Epic1);

        Epic epic2 = new Epic("Включить свет", "Включить свет нажав на переключатель", TaskStatus.NEW);
        epicId = manager.addEpic(epic2);
        int epic2Id = epicId;

        Subtask subtask1Epic2 = new Subtask("Нажать переключатель"
                , "Нажать на переключатель чтобы включился свет", TaskStatus.NEW, epicId);
        int subtask1Epic2Id = manager.addSubtask(subtask1Epic2);

        System.out.println(manager.getTasks() + "" + manager.getEpics() + manager.getSubtasks());

        System.out.println(manager.getAllEpicSubtasks(epic1Id));

        Task task = manager.getTaskById(task1Id);
        task.setStatus(TaskStatus.DONE);
        manager.updateTask(task);


        task = manager.getSubtaskById(subtask1Epic2Id);
        task.setStatus(TaskStatus.DONE);
        manager.updateSubtask((Subtask) task);

        for (int i = 0; i < 1; i++) {
            task = manager.getSubtaskById(subtask1Epic1Id);
        }

        manager.getSubtaskById(subtask2Epic1Id);
        Subtask subtask2Epic2 = new Subtask("Помыть пол"
                , "Помыть весь пол тряпкой", TaskStatus.DONE, epicId, subtask2Epic1Id);
        manager.updateSubtask(subtask2Epic2);
        subtask2Epic1.setId(subtask2Epic1Id);

        task = manager.getSubtaskById(subtask2Epic1Id);
        List<Task> history = manager.getHistoryManager();

        System.out.println("История просмотра");
        System.out.println(history);

        System.out.println(manager.getTasks() + "" + manager.getEpics() + manager.getSubtasks());

        manager.removeSubtask(subtask2Epic1Id);
        manager.removeEpic(epic2Id);
        System.out.println(manager.getTasks() + "" + manager.getEpics() + manager.getSubtasks());

        manager.removeAllTasks();
        System.out.println(manager.getTasks() + "" + manager.getEpics() + manager.getSubtasks());

    }
}

