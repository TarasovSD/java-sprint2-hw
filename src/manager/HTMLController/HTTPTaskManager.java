
package manager.HTMLController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class HTTPTaskManager extends FileBackedTasksManager {
    private String url;
    private KVTaskClient client;
    private Gson gson = getGson();
    private String taskToUpdate = "TASK";
    private String subtask = "SUBTASK";
    private String epicToUpdate = "EPIC";
    private String historyKey = "HISTORY";

    public HTTPTaskManager(String url) {
        this.client = new KVTaskClient(url);
    }

    public HTTPTaskManager(String url, boolean load) {
        this.url = url;
        this.client = new KVTaskClient(url);
        if (load) {
            load();
        }
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }

    public static HTTPTaskManager loadFromServer(String url) {
        final HTTPTaskManager manager = new HTTPTaskManager(url, true);
        return manager;
    }

    @Override
    public void save() {
        try {
            String jsonTask = gson.toJson(tasks);
            client.put(taskToUpdate, jsonTask);
            String jsonSubtask = gson.toJson(subtasks);
            client.put(subtask, jsonSubtask);
            String jsonEpic = gson.toJson(epics);
            client.put(epicToUpdate, jsonEpic);
            List<Task> history = historyManager.getHistory();
            String jsonHistory = gson.toJson(history.stream().map(Task::getId).collect(Collectors.toList()));
            client.put(historyKey, jsonHistory);
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Override
    public void load() {
        try {
            String jsonTasks = client.load(taskToUpdate);
            final HashMap<Integer, Task> restoredTasks = gson.fromJson(jsonTasks,
                    new TypeToken<HashMap<Integer, Task>>() {
                    }.getType());
            for (Map.Entry<Integer, Task> entry : restoredTasks.entrySet()) {
                findingIntersectionsAndAddingTask(entry.getValue());
                tasks.put(entry.getKey(), entry.getValue());
            }
            String jsonEpics = client.load(epicToUpdate);
            final HashMap<Integer, Epic> restoredEpics = gson.fromJson(jsonEpics,
                    new TypeToken<HashMap<Integer, Epic>>() {
                    }.getType());
            for (Map.Entry<Integer, Epic> entry : restoredEpics.entrySet()) {
                epics.put(entry.getKey(), entry.getValue());
            }
            String jsonSubtasks = client.load(subtask);
            final HashMap<Integer, Subtask> restoredSubtasks = gson.fromJson(jsonSubtasks,
                    new TypeToken<HashMap<Integer, Subtask>>() {
                    }.getType());
            for (Map.Entry<Integer, Subtask> entry : restoredSubtasks.entrySet()) {
                findingIntersectionsAndAddingTask(entry.getValue());
                subtasks.put(entry.getKey(), entry.getValue());
            }
            String jsonHistory = client.load(historyKey);
            final List<Integer> historyId = gson.fromJson(jsonHistory, new TypeToken<List<Integer>>() {
            }.getType());
            int a = 0;
            while (a < historyId.size()) {
                List<Task> allTasks = getAllTasks();
                List<Subtask> allSubtasks = getAllSubtasks();
                List<Epic> allEpics = getAllEpics();
                for (Task task : allTasks) {
                    if (historyId.get(a) == task.getId()) {
                        historyManager.add(task);
                    }
                }
                for (Subtask subtask : allSubtasks) {
                    if (historyId.get(a) == subtask.getId()) {
                        historyManager.add(subtask);
                    }
                }
                for (Epic epic : allEpics) {
                    if (historyId.get(a) == epic.getId()) {
                        historyManager.add(epic);
                    }
                }
                a++;
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Override
    public Task createTask(Task newTask) {
        LocalDateTime start = null;
        if (newTask.getStart() != null) {
            start = newTask.getStart();
        }
        int duration = 0;
        if (newTask.getDuration() != 0) {
            duration = newTask.getDuration();
        }
        LocalDateTime end = start.plusMinutes(duration);
        newTask.setEnd(end);
        final Task task = super.createTask(newTask);
        return task;
    }

    @Override
    public Task updateTask(Task taskToUpdate) {

        LocalDateTime start = null;
        if (taskToUpdate.getStart() != null) {
            start = taskToUpdate.getStart();
        }
        int duration = 0;
        if (taskToUpdate.getDuration() != 0) {
            duration = taskToUpdate.getDuration();

        }
        LocalDateTime end = start.plusMinutes(duration);
        taskToUpdate.setEnd(end);
        final Task task = super.updateTask(taskToUpdate);
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask newSubtask) {
        LocalDateTime start = null;
        if (newSubtask.getStart() != null) {
            start = newSubtask.getStart();
        }
        int duration = 0;
        if (newSubtask.getDuration() != 0) {
            duration = newSubtask.getDuration();
        }
        LocalDateTime end = start.plusMinutes(duration);
        newSubtask.setEnd(end);
        final Subtask subtask = super.createSubtask(newSubtask);
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask taskToUpdate) {
        LocalDateTime start = null;
        if (taskToUpdate.getStart() != null) {
            start = taskToUpdate.getStart();
        }
        int duration = 0;
        if (taskToUpdate.getDuration() != 0) {
            duration = taskToUpdate.getDuration();
        }
        LocalDateTime end = start.plusMinutes(duration);
        taskToUpdate.setEnd(end);
        final Subtask subtask = super.updateSubtask(taskToUpdate);
        return subtask;
    }

    @Override
    public Epic createEpic(Epic newEpic) {
        final Epic epic = super.createEpic(newEpic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epicToUpdate) {
        if (epicToUpdate.getSubtasks() == null || epicToUpdate.getSubtasks().isEmpty()) {
            epicToUpdate.setSubtasks(new ArrayList<>());
        } else if (epicToUpdate.getSubtasks().size() == 1) {
            Subtask subtask = epicToUpdate.getSubtasks().get(0);
            epicToUpdate.setStart(subtask.getStart());
            epicToUpdate.setDuration(subtask.getDuration());
            epicToUpdate.setEnd(subtask.getEnd());
        } else {
            List<Subtask> epicSubtasks = epicToUpdate.getSubtasks();
            TreeSet<Subtask> sortedByTimeListOfSubtasks = new TreeSet<>(Comparator.comparing(Task::getStart));
            int subtaskEpicDuration;
            sortedByTimeListOfSubtasks.addAll(epicSubtasks);
            if (!sortedByTimeListOfSubtasks.isEmpty()) {
                epicToUpdate.setStart(sortedByTimeListOfSubtasks.first().getStart());
                epicToUpdate.setEnd(sortedByTimeListOfSubtasks.last().getEnd());
            }
            int epicDuration = 0;
            for (Subtask subtask : epicSubtasks) {
                subtaskEpicDuration = subtask.getDuration();
                epicDuration = epicDuration + subtaskEpicDuration;
            }
            epicToUpdate.setDuration(epicDuration);
        }
        final Epic epic = super.updateEpic(epicToUpdate);
        return epic;
    }
}
