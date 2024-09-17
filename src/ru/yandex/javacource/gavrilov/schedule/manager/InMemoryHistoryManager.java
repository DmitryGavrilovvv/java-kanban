package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();

    @Override
    public void addTaskInHistory(Task task) {
        if (task == null) {
            return;
        }
        Node node = new Node(task);
        removeNode(node);
        linkLast(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        if (!history.containsKey(id)) {
            return;
        }
        removeNode(history.get(id));
        history.remove(id);
    }

    @Override
    public void clear() {
        history.clear();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(int id, Node node) {
        if (node == null) {
            return;
        }
        history.put(id, node);
        if (Node.getHead() == null) {
            Node.setHead(node);
        } else if (Node.getHead().equals(Node.getTail())) {
            Node.getHead().setNext(node);
            node.setPrevious(Node.getHead());
        } else {
            node.setPrevious(Node.getTail());
            ;
            Node.getTail().setNext(node);
        }
        Node.setTail(node);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = Node.getHead();
        for (int i = 0; i < history.size(); i++) {
            tasks.add(node.getValue());
            if (node.getNext() != null) {
                node = node.getNext();
            }
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (history.containsKey(node.getValue().getId())) {
            Node savedNode = history.get(node.getValue().getId());
            if (savedNode.equals(Node.getHead())) {
                Node.setHead(null);
            }
            if (savedNode.getPrevious() != null) {
                savedNode.getPrevious().setNext(savedNode.getNext());
            }
            if (savedNode.getNext() != null) {
                savedNode.getNext().setPrevious(savedNode.getPrevious());
            }
            history.remove(node.getValue().getId());
        }
    }
}