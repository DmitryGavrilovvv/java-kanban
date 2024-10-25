package ru.yandex.javacource.gavrilov.schedule;

import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Manager.getDefault();
        Epic epic = new Epic("e", "d", TaskStatus.NEW);
        Integer id = manager.addEpic(epic);
        Subtask subtask = new Subtask("s", "d", TaskStatus.NEW, id, Duration.ofMinutes(10), LocalDateTime.now());
        Integer idS = manager.addSubtask(subtask);
        Subtask subtask2 = new Subtask("upd", "d", TaskStatus.NEW, id, idS, Duration.ofMinutes(10), LocalDateTime.now());
        manager.updateSubtask(subtask2);
        System.out.println(manager.getSubtaskById(idS));
    }
}

