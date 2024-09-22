package project.manager;

import project.taskType.Task;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> tail;
    private Node<Task> head;

    @Override
    public void add(Task task) {
        if (task != null) {
            linkLast(task);
        } else {
            System.out.println("Ошибка: попытка добавить пустую задачу");
        }
    }

    @Override
    public void remove(Integer id) {
        if (history.containsKey(id)) {
            Node<Task> node = history.remove(id);
            removeNode(node);
        } else {
            System.out.println("Ошибка: попытка удалить несуществующую задачу");
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        if (history.containsKey(task.getId())) {
            removeNode(history.get(task.getId()));
        }
        Node<Task> node = new Node<>(task, tail, null);
        Node<Task> oldTail = tail;
        tail = node;
        history.put(task.getId(), node);
        if (oldTail != null) {
            oldTail.setNext(node);
        } else {
            head = node;
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new LinkedList<>();
        Node<Task> node = head;
        while (node != null) {
            tasks.add(node.getTask());
            node = node.getNext();
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (node != null) {
            if (node.getPrevious() != null) {
                node.getPrevious().setNext(node.getNext());
            } else {
                head = node.getNext();
            }
            if (node.getNext() != null) {
                node.getNext().setPrevious(node.getPrevious());
            } else {
                tail = node.getPrevious();
            }
        } else {
            System.out.println("Ошибка: попытка удалить пустой узел");
        }
    }

    private class Node<T extends Task> {
        T task;
        Node<T> prev;
        Node<T> next;

        public Node(T task, Node<T> prev, Node<T> next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        public T getTask() {
            return task;
        }

        public Node<T> getPrevious() {
            return prev;
        }

        public void setPrevious(Node<T> prev) {
            this.prev = prev;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }
    }
}