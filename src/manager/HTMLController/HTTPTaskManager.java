
package manager.HTMLController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.FileBackedTasksManager;
import manager.Managers;
import manager.TaskManager;
import models.Status;
import models.Task;
import models.TaskTypes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;


public class HTTPTaskManager extends FileBackedTasksManager {
    String url;
    KVTaskClient client;
    final Gson gson = getGson();
    String task = "TASK";
    String subtask = "SUBTASK";
    String epic = "EPIC";

    public HTTPTaskManager(String url) {
        this.url = url;
        this.client = new KVTaskClient(url);
    }

    public static Gson getGson()  {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
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
            client.load(task);
            client.load(subtask);
            client.load(epic);
        } catch (InterruptedException | IOException e) {
            System.out.println("Во время выполнения запроса ресурса по url-адресу: '" + url + "' возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        KVServer server = new KVServer();
        server.start();

        Task newTask = new Task(1, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 6, 0), 20);
        manager.createTask(newTask);

        System.out.println(manager.getAllTasks());

    }
}
