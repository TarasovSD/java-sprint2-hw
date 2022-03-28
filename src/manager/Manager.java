package manager;

import models.Epic;
import models.Subtask;
import models.Task;
import models.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Менеджер задач
 */
public class Manager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private int generatorId = 0;

    /**
     * Генерирует новый id
     *
     * @return - new ID
     */
    private int generateNextId() {
        return ++generatorId;
    }

    /**
     * Создает новую задачу
     *
     * @return - newTask
     */
    public Task createTask(Task newTask) {
        int taskId = generateNextId();
        newTask.setId(taskId);
        tasks.put(taskId, newTask);
        return newTask;
    }

    /**
     * Обновляет задачу
     *
     * @return - taskToUpdate
     */
    public Task updateTask(Task taskToUpdate) {
        int taskId = taskToUpdate.getId();
        Task foundTask = tasks.get(taskId);
        if (foundTask == null) {
            System.out.println("Задача с ID " + taskId + " не найдена!");
            return null;
        }
        tasks.put(taskId, taskToUpdate);
        return taskToUpdate;
    }

    /**
     * Получает задачу по Id
     *
     * @return - task
     */
    public Task getTask(int taskId) {
        return tasks.get(taskId);
    }

    /**
     * Удаляет задачу по id
     *
     * @return - task
     */
    public Task deleteTask(int id) {
        return tasks.remove(id);
    }

    /**
     * Получает список всех задач
     *
     * @return - ArrayList<>(tasks.values())
     */
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Удаляет все задачи
     */
    public void deleteAllTasks() {
        tasks.clear();
    }

    // ------------------------- SUBTASKS -----------------------

    /**
     * Создает подзадачу
     *
     * @return - newSubtask
     */
    public Subtask createSubtask(Subtask newSubtask) {
        Epic epic = getEpic(newSubtask.getEpicID());
        if (epic == null) {
            System.out.println("Эпик не найден!");
            return null;
        }
        int taskId = generateNextId();
        newSubtask.setId(taskId);
        subtasks.put(taskId, newSubtask);
        epic.addSubtask(newSubtask);
        updateEpic(epic);
        return newSubtask;
    }

    /**
     * Обновляет подзадачу
     *
     * @return - taskToUpdate
     */
    public Subtask updateSubtask(Subtask taskToUpdate) {
        int taskId = taskToUpdate.getId();
        Subtask foundTask = subtasks.get(taskId);
        if (foundTask == null) {
            System.out.println("Задача с ID " + taskId + " не найдена!");
            return null;
        }
        if (!Objects.equals(taskToUpdate.getEpicID(), foundTask.getEpicID())) {
            Epic epicToRemoveSubtask = getEpic(foundTask.getEpicID());
            Epic epicToAddSubtask = getEpic(taskToUpdate.getEpicID());
            epicToRemoveSubtask.removeSubtask(foundTask);
            epicToAddSubtask.addSubtask(taskToUpdate);
            updateEpic(epicToRemoveSubtask);
            updateEpic(epicToAddSubtask);
        }
        subtasks.put(taskId, taskToUpdate);
        Epic epic = getEpic(taskToUpdate.getEpicID());
        updateEpic(epic);
        return taskToUpdate;
    }

    /**
     * Получает подзадачу по Id
     *
     * @return - subtask
     */
    public Subtask getSubtask(int taskId) {
        return subtasks.get(taskId);
    }

    /**
     * Удаляет подзадачу по id
     *
     * @return - subtask
     */
    public Subtask deleteSubtask(int id) {
        Subtask subtask = getSubtask(id);
        Epic epic = getEpic(subtask.getEpicID());
        if (epic == null) {
            System.out.println("Эпик не найден!");
            return null;
        }
        epic.removeSubtask(subtask);
        updateEpic(epic);
        return subtasks.remove(id);
    }

    /**
     * Получает список всех подзадач
     *
     * @return - ArrayList<>(subtasks.values())
     */
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /**
     * Удаляет все подзадачи
     */
    public void deleteAllSubtasks() {
        ArrayList<Epic> allEpics = getAllEpics();
        for (Epic epic : allEpics) {
            ArrayList<Subtask> subtasks = epic.getSubtasks();
            for (Subtask subtask : subtasks) {
                deleteSubtask(subtask.getId());
            }
        }
    }

    // ----------------- EPICS ------------------------

    /**
     * Создает новый эпик
     *
     * @return - newEpic
     */
    public Epic createEpic(Epic newEpic) {
        int epicId = generateNextId();
        newEpic.setId(epicId);
        epics.put(epicId, newEpic);
        return newEpic;
    }

    /**
     * Обновляет эпик
     *
     * @return - epicToUpdate
     */
    public Epic updateEpic(Epic epicToUpdate) {
        int epicId = epicToUpdate.getId();
        Epic foundTask = epics.get(epicId);
        if (foundTask == null) {
            System.out.println("Эпик с ID " + epicId + " не найден!");
            return null;
        }
        String computedStatus = computeEpicStatus(epicToUpdate);
        epicToUpdate.setStatus(computedStatus);
        epics.put(epicId, epicToUpdate);
        return epicToUpdate;
    }

    /**
     * Получает эпик по Id
     *
     * @return - epic
     */
    public Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    /**
     * Получает подзадачи эпика по epicId
     *
     * @return - subtasks
     */
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic == null) {
            System.out.println("Эпик не найден!");
            return null;
        }
        return epic.getSubtasks();
    }

    /**
     * Удаляет эпик по Id
     *
     * @return - epics
     */
    public Epic deleteEpic(int id) {
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(id);
        for (Subtask subtask : epicSubtasks) {
            deleteSubtask(subtask.getId());
        }
        return epics.remove(id);
    }

    /**
     * Получает список всех эпиков
     *
     * @return - ArrayList<>(epics.values())
     */
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /**
     * Удаляет все эпики
     */
    public void deleteAllEpics() {
        ArrayList<Epic> allEpics = getAllEpics();
        for (Epic epic : allEpics) {
            deleteEpic(epic.getId());
        }
    }

    /**
     * Вычисляет статус эпика по его подзадачам
     *
     * @return models.Status
     */
    private String computeEpicStatus(Epic epic) {
        ArrayList<Subtask> allSubtasks = epic.getSubtasks();
        // если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW
        // если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
        if (allSubtasks.isEmpty()) {
            return Status.New;
        }
        boolean allSubtasksHaveStatusNew = true;
        boolean allSubtasksHaveStatusDone = true;
        for (Subtask subtask : allSubtasks) {
            if (!Objects.equals(subtask.getStatus(), Status.New)) {
                allSubtasksHaveStatusNew = false;
            }
            if (!Objects.equals(subtask.getStatus(), Status.Done)) {
                allSubtasksHaveStatusDone = false;
            }
        }
        if (allSubtasksHaveStatusNew) {
            return Status.New;
        }
        if (allSubtasksHaveStatusDone) {
            return Status.Done;
        }

        // во всех остальных случаях статус должен быть IN_PROGRESS
        return Status.InProgress;
    }
}
