package manager;

import models.Task;

import java.util.*;

/**
 * История задач в памяти
 */
public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        final Node<Task> node = history.get(task.getId());
        if (node != null) {
            removeNodeFromListAndHistory(node.item.getId());
        }
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        Node<Task> currentNode = first;
        while (currentNode != null) {
            list.add(currentNode.item);
            currentNode = currentNode.next;
        }
        return list;
    }

    @Override
    public void remove(int taskId) {
        removeNodeFromListAndHistory(taskId);
    }

    private void removeNodeFromListAndHistory(int taskId) {
        Node<Task> node = history.remove(taskId);
        if (node == null) {
            return;
        }
        if (node == first && node == last) {
            first = null;
            last = null;
            return;
        } else if (node == first) {
            first = first.next;
            first.prev = null;
            return;
        } else if (node == last) {
            last = last.prev;
            last.next = null;
            return;
        }
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
        node.prev = null;
        node.next = null;
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

    private void linkLast(Task task) {
        if (first == null) {
            first = new Node<>(null, task, null);
            last = first;
            history.put(task.getId(), first);
            return;
        }
        final Node<Task> lastElement = last;
        final Node<Task> newNode = new Node<>(lastElement, task, null);
        lastElement.next = newNode;
        last = newNode;
        history.put(task.getId(), newNode);
    }
}
