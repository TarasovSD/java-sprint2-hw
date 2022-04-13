package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

/**
 * Менеджер задач
 */
public interface Manager {

    List<Task> getHistory();

    /**
     * Создает новую задачу
     *
     * @return - newTask
     */
    Task createTask(Task newTask);

    /**
     * Обновляет задачу
     *
     * @return - taskToUpdate
     */
    Task updateTask(Task taskToUpdate);

    /**
     * Получает задачу по Id
     *
     * @return - task
     */
    Task getTask(int taskId);

    /**
     * Удаляет задачу по id
     *
     * @return - task
     */
    Task deleteTask(int id);

    /**
     * Получает список всех задач
     *
     * @return - List<>(tasks.values())
     */
    List<Task> getAllTasks();

    /**
     * Удаляет все задачи
     */
    void deleteAllTasks();

    /**
     * Создает подзадачу
     *
     * @return - newSubtask
     */
    Subtask createSubtask(Subtask newSubtask);

    /**
     * Обновляет подзадачу
     *
     * @return - taskToUpdate
     */
    Subtask updateSubtask(Subtask taskToUpdate);

    /**
     * Получает подзадачу по Id
     *
     * @return - subtask
     */
    Subtask getSubtask(int taskId);

    /**
     * Удаляет подзадачу по id
     *
     * @return - subtask
     */
    Subtask deleteSubtask(int id);

    /**
     * Получает список всех подзадач
     *
     * @return - List<>(subtasks.values())
     */
    List<Subtask> getAllSubtasks();

    /**
     * Удаляет все подзадачи
     */
    void deleteAllSubtasks();

    /**
     * Создает новый эпик
     *
     * @return - newEpic
     */
    Epic createEpic(Epic newEpic);

    /**
     * Обновляет эпик
     *
     * @return - epicToUpdate
     */
    Epic updateEpic(Epic epicToUpdate);

    /**
     * Получает эпик по Id
     *
     * @return - epic
     */
    Epic getEpic(int epicId);

    /**
     * Получает подзадачи эпика по epicId
     *
     * @return - subtasks
     */
    List<Subtask> getEpicSubtasks(int epicId);

    /**
     * Удаляет эпик по Id
     *
     * @return - epics
     */
    Epic deleteEpic(int id);

    /**
     * Получает список всех эпиков
     *
     * @return - List<>(epics.values())
     */
    List<Epic> getAllEpics();

    /**
     * Удаляет все эпики
     */
    void deleteAllEpics();
}
