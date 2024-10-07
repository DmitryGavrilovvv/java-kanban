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
    private static final String FIRST_STRING = "id;type;name;status;description;epic\n";

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
            fileWriter.write(FIRST_STRING);
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
            epicId = ((Subtask) task).getEpicId();
        }
        String epicIdStr = "";
        if (epicId != null) {
            epicIdStr = epicId.toString();
        }
        Type type = task.getType();

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
                if (manager.generatorId < id) {
                    manager.generatorId = id;
                }
                switch (task.getType()) {
                    case Type.EPIC:
                        manager.epics.put(id, (Epic) task);
                    case Type.SUBTASK:
                        manager.subtasks.put(id, (Subtask) task);
                        manager.epics.get(((Subtask) task).getEpicId()).addSubtaskId(id);
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
        String name = str[2];
        String desc = str[4];
        Integer id = Integer.parseInt(str[0]);
        switch (type) {
            case Type.EPIC -> {
                task = new Epic(name, desc, status, id);
            }
            case Type.SUBTASK -> {
                task = new Subtask(name, desc, status, Integer.parseInt(str[5]), id);
            }
            default -> task = new Task(name, desc, status, id);
        }
        return task;
    }

}
