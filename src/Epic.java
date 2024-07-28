import java.util.ArrayList;
import java.util.HashMap;

public class Epic extends Task{
    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }
    HashMap<Integer,Subtask> subtasks=new HashMap<>();

    public void addOrSetSubtask(Subtask subtask,int index){
        subtasks.put(index,subtask);
        updateEpicStatus();
    }
    public void removeSubtask(Subtask subtask,int index){
        subtasks.remove(index,subtask);
        updateEpicStatus();
    }
    public void updateEpicStatus(){

    }
}

