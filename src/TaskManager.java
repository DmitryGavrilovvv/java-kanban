import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс для управления задачами
 */
public final class TaskManager {
    public int generatorId = 0;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks= new HashMap<>();
    }

    public int addTask(Task task) {
        if (task == null) {
            return -1;
        }
        task.id = ++generatorId;
        tasks.put(task.id, task);
        return task.id;
    }

    public void updateTask(Task task) {
        int id = task.getId();
        Task savedTask = tasks.get(id);
        if (savedTask == null) {
            return;
        }
        tasks.put(id, task);
    }

    public int addEpic(Epic epic) {
        if (epic == null) {
            return -1;
        }
        epic.id = ++generatorId;
        epics.put(epic.id, epic);
        return epic.id;
    }

    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    public int addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return 0;//при возврате null(как в вашем примере) выдаёт ошибку заменил на 0
        }
        final int id = ++generatorId;
        subtask.setId(id);
        subtasks.put(id, subtask);
        epic.addSubtaskId(subtask.getId());
        updateEpicStatus(epic.getId());
        return id;
    }

    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null){
            return;
        }
        subtasks.put(subtask.getId(),subtask);
        updateEpicStatus(epic.getId());

    }

    public void removeById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else if (epics.containsKey(id)) {
            epics.remove(id);
        } else {
            for (Epic epic : epics.values()) {
                epic.removeSubtaskId(id);
            }
            subtasks.remove(id);
        }
    }

    public void removeTaskById(int id){
        tasks.remove(id);
    }

    public void removeEpicById(int id){
        epics.remove(id);
    }

    public void removeSubtaskById(int id){
        tasks.remove(id);
    }

    public void removeTasks(){
        tasks.clear();
    }

    //так как сабтаски не могут существовать без эпиков удаляем и их
    public void removeEpics() {
        epics.clear();
        subtasks.clear();
    }
    public void removeSubtasks(){
        subtasks.clear();
        for (Epic epic:epics.values()){
            ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
            subtasksIds.clear();
            epic.setSubtasksIds(subtasksIds);
        }
    }

    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id){
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id){
        return subtasks.get(id);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getAllEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int id : subtasksIds){
            epicSubtasks.add(subtasks.get(id));
        }
        return epicSubtasks;
    }
    public ArrayList<Object> getAllTasks(){
        ArrayList<Object> AllTasks = new ArrayList<>();
        AllTasks.add( tasks.values());
        AllTasks.add(epics.values());
        AllTasks.add( subtasks.values());
        return AllTasks;
    }


    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (int id : subtasksIds){
            epicSubtasks.add(subtasks.get(id));
        }
        boolean allDone = true;
        boolean allNew = true;
        for (Subtask subtask : epicSubtasks) {
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
            epic.setStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
