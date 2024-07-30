package ru.yandex.javacource.Gavrilov.schedule.task;

import ru.yandex.javacource.Gavrilov.schedule.TaskStatus;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return '\n' + "Subtask{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", idCode=" + id +
                ", epicId=" + epicId +
                '}';
    }
}