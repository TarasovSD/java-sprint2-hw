package manager.HTMLController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.TaskManager;
import models.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HTTPTaskManagerTest {

    HttpClient client = HttpClient.newHttpClient();
    private Gson gson;

    KVServer kvServer;
    HttpTaskServer httpServer;

    void start() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        httpServer = new HttpTaskServer();
        httpServer.start();
    }

    @AfterEach
    void stop() {
        httpServer.stop();
        kvServer.stop();
    }

    @Test
    void save() throws IOException, InterruptedException {
        start();

        Epic epic1 = new Epic(1, TaskTypes.EPIC, "Эпик 1", "Описание эпика 1",
                LocalDateTime.of(2022, 5, 31, 7, 0), 0);
        Subtask subtask1 = new Subtask(2, TaskTypes.SUBTASK, "Сабтаск 1", "Описание сабтаска 1", Status.NEW,
                1, LocalDateTime.of(2022, 5, 31, 8, 0), 20);
        Subtask subtask2 = new Subtask(3, TaskTypes.SUBTASK, "Сабтаск 2", "Описание сабтаска 2", Status.NEW,
                1, LocalDateTime.of(2022, 5, 31, 9, 0), 20);
        Task newTask = new Task(1, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 10, 0), 20);
        gson = getGson();
        String json = gson.toJson(newTask);
        URI url = URI.create("http://localhost:8090/tasks/epic/");
        URI url3 = URI.create("http://localhost:8090/tasks/subtask/");
        URI url6 = URI.create("http://localhost:8090/tasks/task/");
        String jsonEpic1 = gson.toJson(epic1);
        String jsonSubtask1 = gson.toJson(subtask1);
        String jsonSubtask2 = gson.toJson(subtask2);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url6).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        final HttpRequest.BodyPublisher bodyEpic1 = HttpRequest.BodyPublishers.ofString(jsonEpic1);
        HttpRequest requestEpic1 = HttpRequest.newBuilder().uri(url).POST(bodyEpic1).build();
        HttpResponse<String> responseEpic1 = client.send(requestEpic1, HttpResponse.BodyHandlers.ofString());

        final HttpRequest.BodyPublisher bodySubtask1 = HttpRequest.BodyPublishers.ofString(jsonSubtask1);
        HttpRequest requestSubtask1 = HttpRequest.newBuilder().uri(url3).POST(bodySubtask1).build();
        HttpResponse<String> responseSubtask1 = client.send(requestSubtask1, HttpResponse.BodyHandlers.ofString());

        final HttpRequest.BodyPublisher bodySubtask2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest requestSubtask2 = HttpRequest.newBuilder().uri(url3).POST(bodySubtask2).build();
        HttpResponse<String> responseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(200, responseEpic1.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(200, responseSubtask1.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(200, responseSubtask2.statusCode(), "Код ответа сервера должен быть: 200");
    }

    @Test
    void loadFromHTTP() throws IOException {
        start();

        TaskManager manager = Managers.getDefault();

        Task newTask = new Task(1, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 6, 0), 20);
        manager.createTask(newTask);
        Task newTask1 = new Task(2, TaskTypes.TASK, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 7, 0), 20);
        manager.createTask(newTask1);
        Epic epic = new Epic(3, TaskTypes.EPIC, "Эпик", "Описание эпика",
                LocalDateTime.of(2022, 5, 31, 8, 0), 0);
        manager.createEpic(epic);
        Subtask subtask = new Subtask(4, TaskTypes.SUBTASK, "Сабтаск", "Описание сабтаска", Status.NEW,
                3, LocalDateTime.of(2022, 5, 31, 9, 0), 20);
        manager.createSubtask(subtask);
        manager.getTask(1);
        manager.getTask(2);

        TaskManager manager1 = HTTPTaskManager.loadFromServer("http://localhost:8060/");

        assertEquals(2, manager1.getHistory().size(), "Размер списка истории должен быть: 2");
        assertNotNull(manager1.getTask(1), "newTask не должна быть null");
        assertNotNull(manager1.getTask(2));
        assertNotNull(manager1.getEpic(3));
        assertNotNull(manager1.getSubtask(4));
        assertEquals(2, manager1.getAllTasks().size(), "Размер списка задач должен быть: 2");
        assertEquals(1, manager1.getAllEpics().size(), "Размер списка эпиков должен быть: 1");
        assertEquals(1, manager1.getAllSubtasks().size(), "Размер списка подзадач должен быть: 1");
        assertEquals("Задача 1", manager1.getTask(1).getName(),
                "Наименование задачи должно быть: Задача 1");
        assertEquals("Задача 2", manager1.getTask(2).getName(),
                "Наименование задачи должно быть: Задача 2");
        assertEquals("Эпик", manager1.getEpic(3).getName(),
                "Наименование эпика должно быть: Эпик");
        assertEquals("Сабтаск", manager1.getSubtask(4).getName(),
                "Наименование подзадачи должно быть: Сабтаск");
        assertEquals(3, manager1.getSubtask(4).getEpicID(),
                "Id эпика подзадачи должен быть: 3");
        assertEquals(1, manager1.getEpic(3).getSubtasks().size(),
                "Размер списка подзадач эпика должен быть: 1");
        assertEquals(manager1.getSubtask(4), manager1.getEpic(3).getSubtasks().get(0),
                "Подзадача из списка подзадач и подзадача из списка подзадач эпика должны совпадать");
        assertEquals(manager1.getEpic(3).getStart(), manager1.getSubtask(4).getStart(),
                "Время начала эпика и начала подзадачи должны совпадать");
        assertEquals(manager1.getEpic(3).getEnd(), manager1.getSubtask(4).getEnd(),
                "Время конца эпика и конца подзадачи должны совпадать");
        assertEquals(manager1.getEpic(3).getDuration(), manager1.getSubtask(4).getDuration(),
                "Продолжительность задачи и продолжительность эпика должны совпадать");

    }

    private static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}