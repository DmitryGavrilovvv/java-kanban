package ru.yandex.javacource.gavrilov.schedule.task;

import ru.yandex.javacource.gavrilov.schedule.manager.Type;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected Integer id;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description, TaskStatus status, Integer id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Type getType() {
        return Type.TASK;
    }

    @Override
    public String toString() {
        return '\n' + "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", idCode=" + id +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Task task = (Task) obj;
        return Objects.equals(id, task.id)
                && name.equals(task.name)
                && description.equals(task.description)
                && status.equals(task.status);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode() * 11;
        result = name.hashCode() * 11 + result;
        result = description.hashCode() * 11 + result;
        result = status.hashCode() * 11 + result;
        return result;
    }
}
