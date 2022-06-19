
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
            for (int i : historyId) {
                List<Task> allTasks = getAllTasks();
                List<Subtask> allSubtasks = getAllSubtasks();
                List<Epic> allEpics = getAllEpics();
                for (Task task : allTasks) {
                    if (i == task.getId()) {
                        historyManager.add(task);
                    }
                }
                for (Subtask subtask : allSubtasks) {
                    if (i == subtask.getId()) {
                        historyManager.add(subtask);
                    }
                }
                for (Epic epic : allEpics) {
                    if (i == epic.getId()) {
                        historyManager.add(epic);
                    }
                }
            }
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}
