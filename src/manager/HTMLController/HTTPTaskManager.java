
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HTTPTaskManager extends FileBackedTasksManager {
    String url;
    KVTaskClient client;
    Gson gson = getGson();
    String task = "TASK";
    String subtask = "SUBTASK";
    String epic = "EPIC";

    String historyKey = "HISTORY";

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

    public static HTTPTaskManager loadFromFile(String url) {
        final HTTPTaskManager manager = new HTTPTaskManager(url, true);
        return manager;
    }

    @Override
    public void save() {
        try {
            String jsonTask = gson.toJson(tasks);
            client.put(task, jsonTask);
            String jsonSubtask = gson.toJson(subtasks);
            client.put(subtask, jsonSubtask);
            String jsonEpic = gson.toJson(epics);
            client.put(epic, jsonEpic);
            List<Task> history = historyManager.getHistory();
            int size = history.size();
            int[] massiveHistoryId = new int[size];
            for (int i = 0; i < history.size(); i++) {
                int id = history.get(i).getId();
                massiveHistoryId[i] = id;
            }
            String jsonHistory = gson.toJson(massiveHistoryId);
            client.put(historyKey, jsonHistory);
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    @Override
    public void load() {
        try {
            String jsonTasks = client.load(task);
            final HashMap<Integer, Task> restoredTasks = gson.fromJson(jsonTasks,
                    new TypeToken<HashMap<Integer, Task>>() {
                    }.getType());
            for (Map.Entry<Integer, Task> entry : restoredTasks.entrySet()) {
                findingIntersectionsAndAddingTask(entry.getValue());
                tasks.put(entry.getKey(), entry.getValue());
            }
            String jsonEpics = client.load(epic);
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
            final int[] massiveHistoryId = gson.fromJson(jsonHistory, int[].class);
            int a = 0;
            while (a < massiveHistoryId.length) {
                List<Task> allTasks = getAllTasks();
                List<Subtask> allSubtasks = getAllSubtasks();
                List<Epic> allEpics = getAllEpics();
                for (Task task : allTasks) {
                    if (massiveHistoryId[a] == task.getId()) {
                        historyManager.add(task);
                    }
                }
                for (Subtask subtask : allSubtasks) {
                    if (massiveHistoryId[a] == subtask.getId()) {
                        historyManager.add(subtask);
                    }
                }
                for (Epic epic : allEpics) {
                    if (massiveHistoryId[a] == epic.getId()) {
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
}
