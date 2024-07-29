import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(Integer subtaskId) {
        subtasksIds.remove(subtaskId);
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    @Override
    public String toString() {
        return '\n' + "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", idCode=" + id +
                ", subtaskIds=" + subtasksIds +
                '}';
    }
}

