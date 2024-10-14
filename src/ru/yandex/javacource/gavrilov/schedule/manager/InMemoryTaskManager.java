package ru.yandex.javacource.gavrilov.schedule.manager;

import java.time.Duration;
import java.util.*;

import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;
import ru.yandex.javacource.gavrilov.schedule.task.*;

/**
 * Класс для управления задачами
 */
public class InMemoryTaskManager implements TaskManager {
    protected int generatorId = 0;
    private final HistoryManager historyManager = Manager.getDefaultHistoryManager();
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    private final Set<Task> prioritizedTasks;
    Comparator<Task> comparator = (Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager() {
        prioritizedTasks = new TreeSet<>(comparator);
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    @Override
    public Integer addTask(Task task) {
        if (task == null) {
            return null;
        }
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        addTaskInPrioritizedTasks(task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.remove(savedTask);
        addTaskInPrioritizedTasks(task);
    }

    @Override
    public Integer addEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(++generatorId);
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
        if (subtask == null) {
            return null;
        }
        subtask.setId(++generatorId);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }
        subtasks.put(subtask.getId(), subtask);
        addTaskInPrioritizedTasks(subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        updateEpicTime(epic);
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
        prioritizedTasks.remove(savedSubtask);
        addTaskInPrioritizedTasks(subtask);
        updateEpicStatus(epic.getId());
        updateEpicTime(epic);

    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.get(id);
        historyManager.remove(id);
        tasks.remove(id);
        prioritizedTasks.remove(task);
        task.setId(null);
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        prioritizedTasks.remove(epic);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasksIds()) {
            subtasks.remove(subtaskId);
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.remove(id);
        }
        epic.setId(null);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask == null) {
            return;
        }
        prioritizedTasks.remove(subtask);
        historyManager.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtaskId(id);
        updateEpicStatus(epic.getId());
        updateEpicTime(epic);
        subtask.setId(null);
    }

    @Override
    public void removeTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
        epics.clear();
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.cleanSubtaskIds();
            updateEpicStatus(epic.getId());
            updateEpicTime(epic);
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        historyManager.clear();
        prioritizedTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        final Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        final Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        final Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> getAllEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        List<Integer> subtasksIds = epic.getSubtasksIds();
        return subtasks.values().stream()
                .filter(subtask -> subtasksIds.contains(subtask.getId()))
                .toList();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void clearHistory() {
        historyManager.clear();
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    private void addTaskInPrioritizedTasks(Task task) {
        if (task.getStartTime() == null || task.getDuration() == null) {
            return;
        }
        if (!isIntersectionTasks(task)) {
            return;
        }
        prioritizedTasks.add(task);
    }

    private boolean isIntersectionTasks(Task task) {
        long i = prioritizedTasks.stream()
                .filter(task1 -> (task1.getEndTime()
                        .isAfter(task.getStartTime()) && task1.getEndTime().isBefore(task.getEndTime()))
                        || (task1.getStartTime().isAfter(task.getStartTime()) && task1.getStartTime()
                        .isBefore(task.getEndTime())))
                .count();
        return i == 0;
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> epicSubtasks = subtasks.values().stream()
                .filter(subtask -> epic.getSubtasksIds().contains(subtask.getId()))
                .toList();

        Optional<Subtask> startTime = epicSubtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .min(comparator);
        if (startTime.isPresent()) {
            Subtask startTimeSubtask = startTime.get();
            if (startTimeSubtask.getStartTime() != null) {
                epic.setStartTime(startTimeSubtask.getStartTime());
            }
        }
        Optional<Subtask> endTime = epicSubtasks.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .max(Comparator.comparing(Task::getEndTime));
        if (endTime.isPresent()) {
            Subtask endTimeSubtask = endTime.get();
            if (endTimeSubtask.getEndTime() != null) {
                epic.setEndTime(endTimeSubtask.getEndTime());
            }
        }
        Duration allDuration = Duration.ofMinutes(0);
        for (Subtask subtask : epicSubtasks) {
            if (subtask.getDuration() != null) {
                allDuration = allDuration.plus(subtask.getDuration());
            }
        }
        epic.setDuration(allDuration);
    }


    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> epicSubtasks = subtasks.values().stream()
                .filter(subtask -> epic.getSubtasksIds().contains(subtask.getId()))
                .toList();
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
