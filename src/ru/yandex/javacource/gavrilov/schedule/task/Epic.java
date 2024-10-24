package ru.yandex.javacource.gavrilov.schedule.task;

import ru.yandex.javacource.gavrilov.schedule.manager.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtasksIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status, null, null);
        type = Type.EPIC;
    }

    public Epic(String name, String description, TaskStatus status, Integer id) {
        super(name, description, status, id, null, null);
        type = Type.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public void cleanSubtaskIds() {
        subtasksIds.clear();
    }

    @Override
    public String toString() {
        return '\n' + "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", idCode=" + id +
                ", subtaskIds=" + subtasksIds +
                '}';
    }
}

