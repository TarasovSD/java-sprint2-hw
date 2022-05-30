package manager;

import models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected int generatorId = 0;

    protected HistoryManager historyManager = Managers.getDefaultHistory();

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
        int taskId = newTask.getId() != null ? newTask.getId() : generateNextId();
        newTask.setId(taskId);
        tasks.put(taskId, newTask);
        return newTask;
    }

    @Override
    public Task updateTask(Task taskToUpdate) {
        int taskId = taskToUpdate.getId();
        Task foundTask = tasks.get(taskId);
        if (foundTask == null) {
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
        historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
    }

    // ------------------------- SUBTASKS -----------------------

    @Override
    public Subtask createSubtask(Subtask newSubtask) {
        Epic epic = getEpicForInner(newSubtask.getEpicID());
        if (epic == null) {
            return null;
        }
        int taskId = newSubtask.getId() != null ? newSubtask.getId() : generateNextId();
        newSubtask.setId(taskId);
        subtasks.put(taskId, newSubtask);
        epic.addSubtask(newSubtask);
        int duration = newSubtask.getDuration();
        epic.setDuration(duration);
        if (epic.getSubtasks().size() == 1) {
            epic.setStart(newSubtask.getStart());
        }
        epic.setEnd(newSubtask.getEnd());
        updateEpic(epic);
        return newSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask taskToUpdate) {
        int taskId = taskToUpdate.getId();
        Subtask foundTask = subtasks.get(taskId);
        if (foundTask == null) {
            return null;
        }
        if (!Objects.equals(taskToUpdate.getEpicID(), foundTask.getEpicID())) {
            Epic epicToRemoveSubtask = getEpicForInner(foundTask.getEpicID());
            Epic epicToAddSubtask = getEpicForInner(taskToUpdate.getEpicID());
            epicToRemoveSubtask.removeSubtask(foundTask);
            epicToAddSubtask.addSubtask(taskToUpdate);
            updateEpic(epicToRemoveSubtask);
            updateEpic(epicToAddSubtask);
        }
        subtasks.put(taskId, taskToUpdate);
        Epic epic = getEpicForInner(taskToUpdate.getEpicID());
        updateEpic(epic);
        return taskToUpdate;
    }

    @Override
    public Subtask getSubtask(int taskId) {
        Subtask subtask = getSubtaskForInner(taskId);
        historyManager.add(subtask);
        return subtask;
    }

    private Subtask getSubtaskForInner(int taskId) {
        return subtasks.get(taskId);
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtask = getSubtaskForInner(id);
        Epic epic = getEpicForInner(subtask.getEpicID());
        if (epic == null) {
            return null;
        }
        epic.removeSubtask(subtask);
        updateEpic(epic);
        historyManager.remove(id);
        return subtasks.remove(id);
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        ArrayList<Epic> allEpics = getAllEpics();

        for (int i = 0; i < allEpics.size(); i++) {
            Epic epic = allEpics.get(i);
            ArrayList<Subtask> allSubtasks = epic.getSubtasks();
            for (int k = 0; k < allSubtasks.size(); k++) {
                Subtask subtask = allSubtasks.get(k);
                deleteSubtask(subtask.getId());
            }
        }
    }

    // ----------------- EPICS ------------------------

    @Override
    public Epic createEpic(Epic newEpic) {
        int epicId = newEpic.getId() != null ? newEpic.getId() : generateNextId();
        newEpic.setId(epicId);
        epics.put(epicId, newEpic);
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic epicToUpdate) {
        int epicId = epicToUpdate.getId();
        Epic foundTask = epics.get(epicId);
        if (foundTask == null) {
            return null;
        }
        Status computedStatus = computeEpicStatus(epicToUpdate);
        epicToUpdate.setStatus(computedStatus);
        epics.put(epicId, epicToUpdate);
        return epicToUpdate;
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = getEpicForInner(epicId);
        historyManager.add(epic);
        return epic;
    }

    private Epic getEpicForInner(int epicId) {
        return epics.get(epicId);
    }


    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = getEpicForInner(epicId);
        if (epic == null) {
            return null;
        }
        return new ArrayList<>(epic.getSubtasks());
    }

    @Override
    public Epic deleteEpic(int id) {
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(id);
        for (Subtask subtask : epicSubtasks) {
            deleteSubtask(subtask.getId());
        }
        historyManager.remove(id);
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
