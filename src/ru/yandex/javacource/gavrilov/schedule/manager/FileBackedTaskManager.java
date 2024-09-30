package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.exception.ManagerSaveException;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.Task;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String First_String = "id;type;name;status;description;epic\n";

    public FileBackedTaskManager(File file) {
        super();
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

    protected void save() {
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
            fileWriter.write(First_String);
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
        if (task.getType().equals(Type.SUBTASK)) {
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
                Integer id = task.getId();
                switch (task.getType()) {
                    case Type.EPIC:
                        manager.epics.put(id, (Epic) task);
                    case Type.SUBTASK:
                        manager.subtasks.put(id, (Subtask) task);
                        manager.getEpicById(((Subtask) task).getEpicId()).addSubtaskId(id);
                    default:
                        manager.tasks.put(id, task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задач из файла");
        }
        return manager;
    }

    private static Task fromString(String value) {
        Task task;
        String[] str = value.split(";");
        TaskStatus status = TaskStatus.valueOf(str[3]);
        Type type = Type.valueOf(str[1]);
        switch (type) {
            case EPIC -> {
                task = new Epic(str[2], str[4], status, Integer.parseInt(str[0]));
            }
            case SUBTASK -> {
                task = new Subtask(str[2], str[4], status, Integer.parseInt(str[5]), Integer.parseInt(str[0]));
            }
            default -> task = new Task(str[2], str[4], status, Integer.parseInt(str[0]));
        }
        return task;
    }

}
