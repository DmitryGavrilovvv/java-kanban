import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс для управления задачами
 */
public final class TaskManager {
    public static int idCode = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public int addTask(Task task) {
        if (task == null) {
            return -1;
        }
        task.idCode = idCode;
        tasks.put(idCode, task);
        TaskManager.idCode += 1;
        return task.idCode;
    }

    public int updateTask(Task task) {
        if (task == null) {
            return -1;
        }
        if (tasks.containsKey(task.idCode)) {
            tasks.put(task.idCode, task);
        } else {
            return -2;
        }
        return 1;
    }

    public int addEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }
        epic.idCode = idCode;
        epics.put(idCode, epic);
        TaskManager.idCode += 1;
        return epic.idCode;
    }

    public int updateEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }
        if (epics.containsKey(epic.idCode)) {
            epics.put(epic.idCode, epic);
        } else {
            return -2;
        }
        return 1;
    }

    public int addSubtask(Subtask subtask) {
        if (subtask == null) {
            return -1;
        }
        subtask.idCode = idCode;
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask, subtask.idCode);
        TaskManager.idCode += 1;
        return subtask.idCode;
    }

    public int updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return -1;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic.getSubtasks().containsKey(subtask.idCode)) {
            epic.updateSubtask(subtask, subtask.idCode);
        } else {
            return -2;
        }
        return 1;
    }

    public void removeById(int idCode) {
        if (tasks.containsKey(idCode)) {
            tasks.remove(idCode);
        } else if (epics.containsKey(idCode)) {
            epics.remove(idCode);
        } else {
            for (Epic epic : epics.values()) {
                epic.removeSubtask(idCode);
            }
        }
    }

    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
    }

    public Task getById(int idCode) {
        if (epics.containsKey(idCode)) {
            return epics.get(idCode);
        } else {
            for (Epic epic : epics.values()) {
                if (epic.getSubtasks().containsKey(idCode)) {
                    return epic.getSubtasks().get(idCode);
                }
            }
        }
        return tasks.get(idCode);
    }

    public ArrayList<Object> getAllTasks() {
        ArrayList<Object> allTasks = new ArrayList<>();
        allTasks.add(tasks);
        allTasks.add(epics);
        return allTasks;
    }

    public ArrayList<Subtask> getAllEpicSubtasks(int epicId) {
        return new ArrayList<>(epics.get(epicId).getSubtasks().values());
    }
}
