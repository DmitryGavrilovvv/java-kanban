package ru.yandex.javacource.Gavrilov.schedule.task;

import ru.yandex.javacource.Gavrilov.schedule.TaskStatus;

public class Task {
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int id;

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return id == task.id
                && name.equals(task.name)
                && description.equals(task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 11 * result + description.hashCode();
        result = 11 * result + status.hashCode();
        result = 11 * result + id;
        return result;
    }
}
