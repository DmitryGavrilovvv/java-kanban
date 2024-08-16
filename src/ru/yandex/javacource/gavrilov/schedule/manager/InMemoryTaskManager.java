package ru.yandex.javacource.gavrilov.schedule.manager;

import java.util.ArrayList;
import java.util.HashMap;

import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;
import ru.yandex.javacource.gavrilov.schedule.task.*;

/**
 * Класс для управления задачами
 */
public final class InMemoryTaskManager implements TaskManager {
    private int generatorId = 0;
    private final HistoryManager historyManager = Manager.getDefaultHistoryManager();
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public Integer addTask(Task task) {
        if (task == null) {
            return null;
        }
        if (task.getId() == null) {
            task.setId(++generatorId);
        }
        while (tasks.containsKey(task.getId())) {
            task.setId(task.getId() + 1);
            ++generatorId;
        }
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    @Override
    public Integer addEpic(Task task) {
        if (!(task instanceof Epic epic)) {
            return null;
        }
        if (epic.getId() == null) {
            epic.setId(++generatorId);
        }
        while (epics.containsKey(epic.getId())) {
            epic.setId(epic.getId() + 1);
            ++generatorId;
        }
        epic.setId(++generatorId);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public Integer addSubtask(Task task) {
        if (!(task instanceof Subtask subtask)) {
            return null;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (subtask.getId() == null) {
            subtask.setId(++generatorId);
        }
        while (subtasks.containsKey(subtask.getId())) {
            subtask.setId(subtask.getId() + 1);
            ++generatorId;
        }
        subtask.setId(++generatorId);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        return subtask.getId();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic.getId());

    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void removeTasks() {
        tasks.clear();
    }

    //так как сабтаски не могут существовать без эпиков удаляем и их
    @Override
    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int id : subtasksIds) {
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int id : subtasksIds) {
            epicSubtasks.add(subtasks.get(id));
        }
        boolean allDone = true;
        boolean allNew = true;
        for (Subtask subtask : epicSubtasks) {
            if (!subtask.getStatus().equals(TaskStatus.DONE)) {
                allDone = false;
            }
            if (!subtask.getStatus().equals(TaskStatus.NEW)) {
                allNew = false;
            }
            if (!allDone && !allNew) {
                break;
            }
        }
        if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
