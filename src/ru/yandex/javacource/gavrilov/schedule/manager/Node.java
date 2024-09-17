package ru.yandex.javacource.gavrilov.schedule.manager;

import ru.yandex.javacource.gavrilov.schedule.task.Task;

import java.util.Objects;

public class Node {
    private static Node head = null;
    private static Node tail = null;
    private final Task value;
    private Node previous = null;
    private Node next = null;

    public Node(Task task) {
        value = task;
    }

    public static Node getHead() {
        return head;
    }

    public static void setHead(Node node){
        head=node;
    }

    public static Node getTail() {
        return tail;
    }

    public static void setTail(Node node){
        tail=node;
    }

    public void setPrevious(Node node) {
        previous = node;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setNext(Node node) {
        next = node;
    }

    public Node getNext() {
        return next;
    }

    public Task getValue(){
        return value;
    }

    @Override
    public String toString() {
        return '\n' + "Node{" + "value='" + value + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Node node = (Node) obj;
        return Objects.equals(value,node.getValue());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
