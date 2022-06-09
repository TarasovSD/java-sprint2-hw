package manager;

import exception.ManagerSaveException;
import exception.TimeCrossingException;
import models.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static models.TaskTypes.*;

public class FileBackedTasksManager extends InMemoryManager {
    private File file;

    public FileBackedTasksManager() {
        this(new File("task.csv"), false);
    }

    public FileBackedTasksManager(File file) {
        this(file, false);
    }

    public FileBackedTasksManager(File file, boolean load) {
        this.file = file;
        if (load) {
            load();
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        final TreeSet<Task> sortedTasks = super.getPrioritizedTasks();
        return sortedTasks;
    }

    @Override
    public void findingIntersectionsAndAddingTask(Task task) {
        super.findingIntersectionsAndAddingTask(task);
    }

    @Override
    public List<Task> getHistory() {
        final List<Task> tasks = super.getHistory();
        save();
        return tasks;
    }

    @Override
    public Task createTask(Task newTask) {
        final Task task = super.createTask(newTask);
        save();
        return task;
    }

    @Override
    public Task updateTask(Task taskToUpdate) {
        final Task task = super.updateTask(taskToUpdate);
        save();
        return task;
    }

    @Override
    public Task getTask(int taskId) {
        final Task task = super.getTask(taskId);
        save();
        return task;
    }

    @Override
    public Task deleteTask(int id) {
        final Task task = super.deleteTask(id);
        save();
        return task;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        final ArrayList<Task> tasks = super.getAllTasks();
        save();
        return tasks;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask newSubtask) {
        final Subtask subtask = super.createSubtask(newSubtask);
        save();
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask taskToUpdate) {
        final Subtask subtask = super.updateSubtask(taskToUpdate);
        save();
        return subtask;
    }

    @Override
    public Subtask getSubtask(int taskId) {
        final Subtask subtask = super.getSubtask(taskId);
        save();
        return subtask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        final Subtask subtask = super.deleteSubtask(id);
        save();
        return subtask;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        final ArrayList<Subtask> subtasks = super.getAllSubtasks();
        save();
        return subtasks;
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Epic createEpic(Epic newEpic) {
        final Epic epic = super.createEpic(newEpic);
        save();
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epicToUpdate) {
        final Epic epic = super.updateEpic(epicToUpdate);
        save();
        return epic;
    }

    @Override
    public Epic getEpic(int epicId) {
        final Epic epic = super.getEpic(epicId);
        save();
        return epic;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        final ArrayList<Subtask> subtasks = super.getEpicSubtasks(epicId);
        save();
        return subtasks;
    }

    @Override
    public Epic deleteEpic(int id) {
        final Epic epic = super.deleteEpic(id);
        save();
        return epic;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        final ArrayList<Epic> epics = super.getAllEpics();
        save();
        return epics;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    /**
     * Возвращает строку из полученной на входе задачи
     */
    private String toString(Task task) {
        String status = String.valueOf(task.getStatus());
        String taskType = String.valueOf(task.getTaskTypes());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");
        String time = task.getStart() != null ? task.getStart().format(formatter) : "";
        String duration = String.valueOf(task.getDuration());
        String getLineFromTask = null;
        if (taskType.equals("TASK")) {
            getLineFromTask = task.getId() + "," + taskType + "," + task.getName()
                    + "," + status + "," + task.getDescription() + "," + time + "," + duration;
        } else if (taskType.equals("SUBTASK")) {
            Subtask subtask = (Subtask) task;
            getLineFromTask = task.getId() + "," + taskType + "," + task.getName()
                    + "," + status + "," + task.getDescription() + "," + subtask.getEpicID() + "," + time + "," + duration;
        } else if (taskType.equals("EPIC")) {
            getLineFromTask = task.getId() + "," + taskType + "," + task.getName()
                    + "," + status + "," + task.getDescription() + "," + time + "," + duration;
        }
        return getLineFromTask;
    }

    /**
     * Возвращает задачу из полученной на входе строки
     */
    private Task fromString(String value) throws IllegalStateException {
        final String[] fields = value.split(",");
        Task newTask;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");
        switch (TaskTypes.valueOf(fields[1])) {
            case TASK:
                newTask = new Task(Integer.valueOf(fields[0]), TASK, String.valueOf(fields[2]), String.valueOf(fields[4]),
                        Status.valueOf(fields[3]), LocalDateTime.parse(fields[5], formatter), Integer.valueOf(fields[6]));
                createTask(newTask);
                return newTask;
            case EPIC:
                return createEpic(new Epic(Integer.valueOf(fields[0]), EPIC, String.valueOf(fields[2]),
                        String.valueOf(fields[4]), LocalDateTime.parse(fields[5], formatter), Integer.valueOf(fields[6])));
            case SUBTASK:
                newTask = new Subtask(Integer.valueOf(fields[0]), SUBTASK, String.valueOf(fields[2]),
                        String.valueOf(fields[4]), Status.valueOf(fields[3]), Integer.valueOf(fields[5]), LocalDateTime.parse(fields[6], formatter), Integer.valueOf(fields[7]));
                createSubtask((Subtask) newTask);
                return newTask;
            default:
                throw new IllegalStateException("Unexpected value: " + TaskTypes.valueOf(fields[1]));
        }
    }

    /**
     * Возвращает строку из истории задач
     */
    static String toString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getHistory()) {
            sb.append(task.toString());
        }
        return sb.toString();
    }

    /**
     * Возвращает список с id задач из полученной на входе строки
     */
    static List<Integer> historyFromString(String value) {
        final String[] id = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String v : id) {
            history.add(Integer.valueOf(v));
        }
        return history;
    }

    /**
     * Сохраняет в файл
     */
    public void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append("id,type,name,status,description,epic,time,duration");
            writer.append("\n");
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            writer.append("\n");
            List<Task> history = historyManager.getHistory();
            for (int i = 0; i < history.size(); i++) {
                String idToStr = String.valueOf(history.get(i).getId());
                writer.append(idToStr + ",");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    /**
     * Восстанавливает из файла
     */
    public void load() {
        int maxID = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    return;
                }
                if (line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);
                int id = task.getId();
                if (maxID < id) {
                    maxID = id;
                }
            }
            String line = reader.readLine();
            if (line == null) {
                return;
            }
            List<Integer> history = historyFromString(line);
            List<Task> allTasks = getAllTasks();
            List<Subtask> allSubtasks = getAllSubtasks();
            List<Epic> allEpics = getAllEpics();
            for (Integer iD : history) {
                for (Task task : allTasks) {
                    if (iD == task.getId()) {
                        historyManager.add(task);
                    }
                    for (Subtask subtask : allSubtasks) {
                        if (iD == subtask.getId()) {
                            historyManager.add(subtask);
                        }
                    }
                    for (Epic epic : allEpics) {
                        if (iD == epic.getId()) {
                            historyManager.add(epic);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        generatorId = maxID;
    }

    /**
     * Читает из файла
     */
    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager manager = new FileBackedTasksManager(file, true);
        return manager;
    }
}