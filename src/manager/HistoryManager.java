package manager;

import models.Task;

import java.util.List;

/**
 * История задач
 */
public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}
