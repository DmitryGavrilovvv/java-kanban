package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Integer addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Integer addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save();
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save();
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(file)) {
            List<String> stringsForSave = new ArrayList<>();
            for (Task task : tasks.values()) {
                stringsForSave.add(toString(task));
            }
            for (Epic epic : epics.values()) {
                stringsForSave.add(toString(epic));
            }
            for (Subtask subtask : subtasks.values()) {
                stringsForSave.add(toString(subtask));
            }
            String firstString = "id;type;name;status;description;epic\n";
            fileWriter.write(firstString);
            for (String str : stringsForSave) {
                fileWriter.write(str);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ManagerSaveException("Ошибка сохранения задач в файл");
        }
    }

    private String toString(Task task) {
        Integer epicId = null;
        if (subtasks.containsKey(task.getId())) {
            epicId = subtasks.get(task.getId()).getEpicId();
        }
        String epicIdStr = " ";
        if (epicId != null) {
            epicIdStr = epicId.toString();
        }
        Type type;
        if (subtasks.containsKey(task.getId())) {
            type = Type.SUBTASK;
        } else if (epics.containsKey(task.getId())) {
            type = Type.EPIC;
        } else {
            type = Type.TASK;
        }

        return String.join(";", task.getId().toString(), type.toString(), task.getName(), task.getStatus().toString(), task.getDescription(),
                epicIdStr, "\n");
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (Reader fileReader = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fileReader);
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else {
                    manager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        Task task;
        String[] str = value.split(";");
        TaskStatus status;
        if (str[3].equals("DONE")) {
            status = TaskStatus.DONE;
        } else if (str[3].equals("IN_PROGRESS")) {
            status = TaskStatus.IN_PROGRESS;
        } else {
            status = TaskStatus.NEW;
        }
        if (str[1].equals("TASK")) {
            task = new Task(str[2], str[4], status, Integer.parseInt(str[0]));
        } else if (str[1].equals("EPIC")) {
            task = new Epic(str[2], str[4], status, Integer.parseInt(str[0]));
        } else {
            task = new Subtask(str[2], str[4], status, Integer.parseInt(str[5]), Integer.parseInt(str[0]));
        }
        return task;
    }

    static class ManagerSaveException extends RuntimeException {
        public ManagerSaveException(final String message) {
            super(message);
        }
    }
}
