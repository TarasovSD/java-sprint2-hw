package manager;

import models.Task;

import java.util.List;

/**
 * История задач
 */
public interface HistoryManager {

    /**
     * Добавляет задачу
     */
    void add(Task task);

    /**
     * Удаляет задачу
     */
    void remove(int taskId);

    /**
     * Возвращает лист с историей
     */
    List<Task> getHistory();
}
