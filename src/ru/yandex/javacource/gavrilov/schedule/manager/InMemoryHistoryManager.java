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
        removeNode(node, task.getId());
        linkLast(task.getId(), node);
    }

    @Override
    public void remove(int id) {
        if (!history.containsKey(id)) {
            return;
        }
        removeNode(history.get(id), id);
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

    private void removeNode(Node node, Integer id) {
        if (history.containsKey(id)) {
            Node savedNode = history.get(id);
            if (savedNode.equals(Node.getHead())) {
                Node.setHead(null);
            } else if (savedNode.equals(Node.getTail())) {
                Node.setTail(savedNode.getPrevious());
            }
            if (savedNode.getPrevious() != null) {
                Node previousNode = savedNode.getPrevious();
                if (savedNode.getNext() != null) {
                    previousNode.setNext(savedNode.getNext());
                } else {
                    previousNode.setNext(null);
                }
            }
            if (savedNode.getNext() != null) {
                Node nextNode = savedNode.getNext();
                if (savedNode.getPrevious() != null) {
                    nextNode.setPrevious(savedNode.getPrevious());
                } else {
                    node.setPrevious(null);
                }
                nextNode.setPrevious(savedNode.getPrevious());
            }
            history.remove(node.getValue().getId());
        }
    }
}