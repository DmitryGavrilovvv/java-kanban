package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> history = new HashMap<>();
    private Node head =null;
    private Node tail = null;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        final int id = task.getId();
        Node node = new Node(task);
        remove(id);
        linkLast(node);
        history.put(id, node);
    }

    @Override
    public void remove(int id) {
        final Node node = history.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    @Override
    public void clear() {
        history.clear();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast( Node node) {
        if (node == null) {
            return;
        }
        if (head == null) {
            head = node;
        } else if (head.equals(tail)) {
            head.setNext(node);
            node.setPrevious(head);
        } else {
            node.setPrevious(tail);
            tail.setNext(node);
        }
        tail = node;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();

        Node node = head;
        while (node != null) {
            tasks.add(node.getValue());
            node = node.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node.getPrevious() != null) {
            node.getPrevious().setNext(node.getNext());
            if (node.getNext() == null) {
                tail = node.getPrevious();
            } else {
                node.getNext().setPrevious(node.getPrevious());
            }
        } else {
            head = node.getNext();
            if (head == null) {
                tail = null;
            } else {
                head.setPrevious(null);
            }
        }
    }
}