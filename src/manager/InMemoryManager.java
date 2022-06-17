package manager;

import exception.TimeCrossingException;
import models.Epic;
import models.Status;
import models.Subtask;
import models.Task;

import java.util.*;

public class InMemoryManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected int generatorId = 0;

    protected HistoryManager historyManager = Managers.getDefaultHistory();

    Comparator<Task> comparator = (o1, o2) -> {
        if (o1.getStart() == null) {
            return 1;
        } else if (o2.getStart() == null) {
            return -1;
        } else {
            return o1.getStart().compareTo(o2.getStart());
        }
    };

    //    protected TreeSet<Task> sortedByTimeListOfTasks = new TreeSet<>(Comparator.comparing(Task::getStart));
    protected TreeSet<Task> sortedByTimeListOfTasks = new TreeSet<>(comparator);

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
    public TreeSet<Task> getPrioritizedTasks() {
        return sortedByTimeListOfTasks;
    }

    @Override
    public void findingIntersectionsAndAddingTask(Task task) {
        if (task.getStart() == null) {
            sortedByTimeListOfTasks.add(task);
            return;
        }
        for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
            if (task.getStart().isAfter(entry.getValue().getStart()) &&
                    task.getStart().isBefore(entry.getValue().getEnd())) {
                throw new TimeCrossingException("Задача пересекает ранее созданные задачи");
            } else if (task.getEnd().isAfter(entry.getValue().getStart())
                    && task.getEnd().isBefore(entry.getValue().getEnd())) {
                throw new TimeCrossingException("Задача пересекает ранее созданные задачи");
            }
        }
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            if (task.getStart().isAfter(entry.getValue().getStart()) &&
                    task.getStart().isBefore(entry.getValue().getEnd())) {
                throw new TimeCrossingException("Подзадача пересекает ранее созданные задачи");
            } else if (task.getEnd().isAfter(entry.getValue().getStart())
                    && task.getEnd().isBefore(entry.getValue().getEnd())) {
                throw new TimeCrossingException("Подзадача пересекает ранее созданные задачи");
            }
        }
        sortedByTimeListOfTasks.add(task);
    }

    @Override
    public Task createTask(Task newTask) {
        int taskId = newTask.getId() != null ? newTask.getId() : generateNextId();
        newTask.setId(taskId);
        findingIntersectionsAndAddingTask(newTask);
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
        findingIntersectionsAndAddingTask(taskToUpdate);
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
        sortedByTimeListOfTasks.remove(tasks.get(id));
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
            sortedByTimeListOfTasks.remove(tasks.get(taskId));
        }
        tasks.clear();
    }

    // ------------------------- SUBTASKS -----------------------

    @Override
    public Subtask createSubtask(Subtask newSubtask) {
        findingIntersectionsAndAddingTask(newSubtask);
        Epic epic = getEpicForInner(newSubtask.getEpicID());
        if (epic == null) {
            return null;
        }
        int taskId = newSubtask.getId() != null ? newSubtask.getId() : generateNextId();
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
            return null;
        }
        findingIntersectionsAndAddingTask(taskToUpdate);
        subtasks.put(taskId, taskToUpdate);
        Epic epic = getEpicForInner(taskToUpdate.getEpicID());
        epic.removeSubtask(foundTask);
        epic.addSubtask(taskToUpdate);
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
        sortedByTimeListOfTasks.remove(subtasks.get(id));
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
        List<Subtask> epicSubtasks = epicToUpdate.getSubtasks();
        TreeSet<Subtask> sortedByTimeListOfSubtasks = new TreeSet<>(Comparator.comparing(Task::getStart));
        int subtaskEpicDuration;
        sortedByTimeListOfSubtasks.addAll(epicSubtasks);
        if (!sortedByTimeListOfSubtasks.isEmpty()) {
            epicToUpdate.setStart(sortedByTimeListOfSubtasks.first().getStart());
            epicToUpdate.setEnd(sortedByTimeListOfSubtasks.last().getEnd());
        } else {
            epicToUpdate.setStart(null);
            epicToUpdate.setEnd(null);
        }
        int epicDuration = 0;
        for (Subtask subtask : epicSubtasks) {
            subtaskEpicDuration = subtask.getDuration();
            epicDuration = epicDuration + subtaskEpicDuration;
        }
        epicToUpdate.setDuration(epicDuration);
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
