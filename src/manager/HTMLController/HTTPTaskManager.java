
package manager.HTMLController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import models.Status;
import models.Task;
import models.TaskTypes;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;


public class HTTPTaskManager extends FileBackedTasksManager {
    String url;
    KVTaskClient client;
    Gson gson = getGson();
    String task = "TASK";
    String subtask = "SUBTASK";
    String epic = "EPIC";

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

    public static Gson getGson()  {
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
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

    }

    @Override
    public void load() {
        try {
            String jsonTasks = client.load(task);
            System.out.println(jsonTasks);
            String jsonSubtasks = client.load(subtask);
            String jsonEpics = client.load(epic);
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

    }

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer server = new KVServer();
        server.start();
        TaskManager manager = Managers.getDefault();


        Task newTask = new Task(1, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 6, 0), 20);
        manager.createTask(newTask);

        System.out.println(manager.getAllTasks());

        TaskManager manager1 = HTTPTaskManager.loadFromFile("http://localhost:8070/");


    }
}
