public class Task {
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected int idCode;

    Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public String toString() {
        return '\n' + "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", idCode=" + idCode +
                '}';
    }
}
