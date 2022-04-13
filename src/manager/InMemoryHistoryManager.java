package manager;

import models.Task;

import java.util.LinkedList;
import java.util.List;

/**
 * История задач в памяти
 */
public class InMemoryHistoryManager implements HistoryManager {
    LinkedList<Task> history = new LinkedList<>();
    int maxHistorySize = 10;

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > maxHistorySize) {
            history.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
