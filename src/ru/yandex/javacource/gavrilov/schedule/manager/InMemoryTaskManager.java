package ru.yandex.javacource.gavrilov.schedule.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;
import ru.yandex.javacource.gavrilov.schedule.task.*;

/**
 * Класс для управления задачами
 */
public final class InMemoryTaskManager implements TaskManager {
    private int generatorId = 0;
    private final HistoryManager historyManager = Manager.getDefaultHistoryManager();
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public Integer addTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return null;
        }
        if (task.getId() == null) {
            task.setId(++generatorId);
        }

        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public Integer addEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return null;
        }
        if (epic.getId() == null) {
            epic.setId(++generatorId);
        }
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        final Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        epic.setSubtasksIds(savedEpic.getSubtasksIds());
        epic.setStatus(savedEpic.getStatus());
        epics.put(epic.getId(), epic);
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            return null;
        }
        if (subtask.getId() == null) {
            subtask.setId(++generatorId);
        }
        Epic epic = epics.get(subtask.getEpicId());
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
        tasks.get(id).setId(null);
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic == null) {
            return;
        }
        epic.setId(null);
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        historyManager.remove(id);
        if (subtask == null) {
            return;
        }
        subtask.setId(null);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicStatus(epic.getId());
    }

    @Override
    public void removeTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
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
        historyManager.clear();
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        historyManager.addTaskInHistory(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);
        historyManager.addTaskInHistory(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.addTaskInHistory(subtask);
        return subtask;
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
    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
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
