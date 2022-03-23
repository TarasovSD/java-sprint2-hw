import java.util.ArrayList;
import java.util.HashMap;

/**
 * Менеджер задач
 */
public class Manager {
    private HashMap<Integer, Task> tasks =new HashMap<>();
    private HashMap<Integer, Subtask> subtasks =new HashMap<>();
    private HashMap<Integer, Epic> epics =new HashMap<>();
    private int generatorId = 0;

    /**
     * Метод для получения списка задач
     * @return
     */
    public ArrayList<Task> getListOfAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Метод для удаления всех задач
     */
    public void deleteAllTasks() {
        tasks.clear();
    }

    /**
     * Метод для получения задачи по идентификатору
     * @return
     */
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    /**
     * Метод для создания задачи
     * @return
     */
    public Task createTask(Task task) {
        task.setId(++generatorId);
        tasks.put(task.getId(), task);
        return task;
    }

    /**
     * Метод для обновления задачи
     */
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    /**
     * Метод для удаления задачи по идентификатору
     */
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    /**
     * Метод для получения списка подзадач
     * @return
     */
    public ArrayList<Subtask> getListOfAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Метод для удаления всех подзадач
     */
    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    /**
     * Метод для получения подзадачи по идентификатору
     * @return
     */
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    /**
     * Метод для создания подзадачи
     * @return
     */
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(++generatorId);
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    /**
     * Метод для обновления подзадачи
     */
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }
        tasks.put(subtask.getId(), subtask);
    }

    /**
     * Метод для удаления подзадачи по идентификатору
     */
    public void deleteSubtaskById(int id) {
        subtasks.remove(id);
    }
    /**
     * Метод для получения списка эпиков
     * @return
     */
    public ArrayList<Epic> getListOfAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Метод для удаления всех эпиков
     */
    public void deleteAllEpics() {
        epics.clear();
    }

    /**
     * Метод для получения эпика по идентификатору
     * @return
     */
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    /**
     * Метод для создания эпика
     * @return
     */
    public Epic createEpic(Epic epic) {
        epic.setId(++generatorId);
        tasks.put(epic.getId(), epic);
        return epic;
    }

    /**
     * Метод для обновления эпика
     */
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }
        tasks.put(epic.getId(), epic);
    }

    /**
     * Метод для удаления эпика по идентификатору
     */
    public void deleteEpicById(int id) {
        epics.remove(id);
    }

}
