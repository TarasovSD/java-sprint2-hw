package manager;

import models.Task;

import java.util.*;

/**
 * История задач в памяти
 */
public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node<Task>> history = new HashMap<>();
    Node<Task> first;
    Node<Task> last;

    int maxHistorySize = 10;

    @Override
    public void add(Task task) {
        final Node<Task> node = history.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        Node<Task> node = first;
        while (node != null) {
            //Todo add to list
            list.add(node.item);
        }
        return list;
    }

    @Override
    public void remove(Task task) {
        final Node<Task> node = history.remove(task.getId());
        if (node != null) {
            removeNode(node);
        }
    }

    private void removeNode(Node<Task> node) {
        if (node == first) {
            first = first.next;
            first.prev = null;
            return;
        }
        if (node == last) {
            last = last.prev;
            last.next = null;
            return;
        }
        //Todo
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    void linkLast(Task e) {
        final Node<Task> lastElement = last;
        final Node<Task> newNode = new Node<>(lastElement, e, null);
        last = newNode;
        if (lastElement == null)
            first = newNode;
        else
            lastElement.next = newNode;
        history.put(e.getId(), newNode);
    }
}
