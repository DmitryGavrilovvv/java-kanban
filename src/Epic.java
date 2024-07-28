import java.util.HashMap;

public final class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask, int index) {
        subtasks.put(index, subtask);
        updateEpicStatus();
    }

    public void updateSubtask(Subtask subtask, int index) {
        subtasks.put(index, subtask);
        updateEpicStatus();
    }

    public void removeSubtask(int index) {
        subtasks.remove(index);
        updateEpicStatus();
    }

    private void updateEpicStatus() {
        boolean allDone = true;
        boolean allNew = true;
        for (Subtask subtask : subtasks.values()) {
            if (!subtask.status.equals(TaskStatus.DONE)) {
                allDone = false;
            }
            if (!subtask.status.equals(TaskStatus.NEW)) {
                allNew = false;
            }
            if (!allDone && !allNew) {
                break;
            }
        }
        if (allDone) {
            status = TaskStatus.DONE;
        } else if (allNew) {
            status = TaskStatus.NEW;
        } else {
            status = TaskStatus.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return '\n' + "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", idCode=" + idCode +
                ", subtasks=" + subtasks +
                '}';
    }
}

