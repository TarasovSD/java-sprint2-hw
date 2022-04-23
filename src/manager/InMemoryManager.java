package manager;

import models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int generatorId = 0;

    HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /**
     * Генерирует новый id
     *
     * @return - new ID
     */
    private int generateNextId() {
        return ++generatorId;
    }

    @Override
    public Task createTask(Task newTask) {
        int taskId = generateNextId();
        newTask.setId(taskId);
        tasks.put(taskId, newTask);
        return newTask;
    }

    @Override
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

    @Override
    public Task getTask(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task deleteTask(int id) {
        historyManager.remove(tasks.get(id));
        return tasks.remove(id);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    // ------------------------- SUBTASKS -----------------------

    @Override
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

    @Override
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

    @Override
    public Subtask getSubtask(int taskId) {
        Subtask subtask = subtasks.get(taskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
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

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
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

    @Override
    public Epic createEpic(Epic newEpic) {
        int epicId = generateNextId();
        newEpic.setId(epicId);
        epics.put(epicId, newEpic);
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic epicToUpdate) {
        int epicId = epicToUpdate.getId();
        Epic foundTask = epics.get(epicId);
        if (foundTask == null) {
            System.out.println("Эпик с ID " + epicId + " не найден!");
            return null;
        }
        Status computedStatus = computeEpicStatus(epicToUpdate);
        epicToUpdate.setStatus(computedStatus);
        epics.put(epicId, epicToUpdate);
        return epicToUpdate;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic == null) {
            System.out.println("Эпик не найден!");
            return null;
        }
        return epic.getSubtasks();
    }


    @Override
    public Epic deleteEpic(int id) {
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(id);
        for (Subtask subtask : epicSubtasks) {
            deleteSubtask(subtask.getId());
        }
        return epics.remove(id);
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
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
    private Status computeEpicStatus(Epic epic) {
        ArrayList<Subtask> allSubtasks = epic.getSubtasks();
        // если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW
        // если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
        if (allSubtasks.isEmpty()) {
            return Status.NEW;
        }
        boolean allSubtasksHaveStatusNew = true;
        boolean allSubtasksHaveStatusDone = true;
        for (Subtask subtask : allSubtasks) {
            if (!Objects.equals(subtask.getStatus(), Status.NEW)) {
                allSubtasksHaveStatusNew = false;
            }
            if (!Objects.equals(subtask.getStatus(), Status.DONE)) {
                allSubtasksHaveStatusDone = false;
            }
        }
        if (allSubtasksHaveStatusNew) {
            return Status.NEW;
        }
        if (allSubtasksHaveStatusDone) {
            return Status.DONE;
        }

        // во всех остальных случаях статус должен быть IN_PROGRESS
        return Status.INPROGRESS;
    }
}
