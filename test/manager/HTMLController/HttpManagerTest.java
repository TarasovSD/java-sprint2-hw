package manager.HTMLController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpManagerTest {
    HttpClient client = HttpClient.newHttpClient();
    private Gson gson;
    KVServer kvServer;
    HttpTaskServer httpServer;
    private TaskManager manager;


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
    void getPrioritizedTasks() throws IOException, InterruptedException {
        start();

        Epic epic1 = new Epic(1, TaskTypes.EPIC, "Эпик 1", "Описание эпика 1",
                LocalDateTime.of(2022, 5, 31, 7, 0), 0);
        Subtask subtask1 = new Subtask(2, TaskTypes.SUBTASK, "Сабтаск 1", "Описание сабтаска 1", Status.NEW,
                1, LocalDateTime.of(2022, 5, 31, 8, 0), 20);
        Subtask subtask2 = new Subtask(3, TaskTypes.SUBTASK, "Сабтаск 2", "Описание сабтаска 2", Status.NEW,
                1, LocalDateTime.of(2022, 5, 31, 9, 0), 20);
        Task newTask = new Task(4, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
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

        manager = HTTPTaskManager.loadFromFile("http://localhost:8060/");

        final TreeSet<Task> sortedTasks = manager.getPrioritizedTasks();

        assertEquals(3, sortedTasks.size(), "Размер списка должен быть: 3");
        assertEquals(subtask1, sortedTasks.first(), "Первый элемент списка должен соответствовать: subtask1");
        assertEquals(newTask, sortedTasks.last(), "Последний элемент списка должен соответствовать: newTask");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        start();

        Epic epic1 = new Epic(1, TaskTypes.EPIC, "Эпик 1", "Описание эпика 1",
                LocalDateTime.of(2022, 5, 31, 7, 0), 0);
        Subtask subtask1 = new Subtask(2, TaskTypes.SUBTASK, "Сабтаск 1", "Описание сабтаска 1", Status.NEW,
                1, LocalDateTime.of(2022, 5, 31, 8, 0), 20);
        Subtask subtask2 = new Subtask(3, TaskTypes.SUBTASK, "Сабтаск 2", "Описание сабтаска 2", Status.NEW,
                1, LocalDateTime.of(2022, 5, 31, 9, 0), 20);
        Task newTask = new Task(4, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 10, 0), 20);
        gson = getGson();
        String json = gson.toJson(newTask);
        URI url = URI.create("http://localhost:8090/tasks/epic/");
        URI url3 = URI.create("http://localhost:8090/tasks/subtask/");
        URI url4 = URI.create("http://localhost:8090/tasks/subtask/?id=2");
        URI url5 = URI.create("http://localhost:8090/tasks/task/?id=4");
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

        HttpRequest taskRequest = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
        HttpRequest subtaskRequest = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());

        manager = HTTPTaskManager.loadFromFile("http://localhost:8060/");

        final List<Task> history = manager.getHistory();

        assertEquals(2, history.size(), "Размер списка должен быть: 2");
        assertEquals(subtask1, manager.getHistory().get(1), "Объуле должен соответствовать: subtask1");
        assertEquals(newTask, manager.getHistory().get(0), "Объуле должен соответствовать: newTask");
    }

    @Test
    void createGetAndDeleteTask() throws IOException, InterruptedException {
        start();

        Task newTask = new Task(1, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 6, 0), 20);
        URI url = URI.create("http://localhost:8090/tasks/task/");
        Task newTask2 = new Task(2, TaskTypes.TASK, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 7, 0), 20);
        gson = getGson();
        String json = gson.toJson(newTask);
        String json2 = gson.toJson(newTask2);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        final HttpRequest.BodyPublisher bodyTask2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest requestTask2 = HttpRequest.newBuilder().uri(url).POST(bodyTask2).build();
        HttpResponse<String> responseTask2 = client.send(requestTask2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(200, responseTask2.statusCode(), "Код ответа сервера должен быть: 200");

        URI url1 = URI.create("http://localhost:8090/tasks/task/?id=1");
        HttpRequest taskRequest = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
        String jsonTask1 = taskResponse.body();
        Task TaskId1 = gson.fromJson(jsonTask1, Task.class);

        URI url4 = URI.create("http://localhost:8090/tasks/task/?id=2");
        HttpRequest task2Request = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> task2Response = client.send(task2Request, HttpResponse.BodyHandlers.ofString());
        String jsonTask2 = task2Response.body();
        Task TaskId2 = gson.fromJson(jsonTask2, Task.class);

        URI url3 = URI.create("http://localhost:8090/tasks/task/");
        HttpRequest tasksRequest = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> tasksResponse = client.send(tasksRequest, HttpResponse.BodyHandlers.ofString());
        String jsonTasks = tasksResponse.body();
        HashMap<Integer, Task> allTasks = gson.fromJson(jsonTasks, new TypeToken<HashMap<Integer, Task>>() {
        }.getType());

        assertEquals(200, taskResponse.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(200, task2Response.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(200, tasksResponse.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(1, TaskId1.getId(), "Id TaskId1 должен быть: 1");
        assertEquals(2, TaskId2.getId(), "Id TaskId2 должен быть: 2");
        assertEquals(2, allTasks.size(), "Количество задач длжно быть: 2");

        HttpRequest task2RequestToDelete = HttpRequest.newBuilder().uri(url4).DELETE().build();
        HttpResponse<String> task2ResponseDelete = client.send(task2RequestToDelete, HttpResponse.BodyHandlers.ofString());
        HttpRequest tasksRequestAfterDelete = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> tasksResponseAfterDelete = client.send(tasksRequestAfterDelete,
                HttpResponse.BodyHandlers.ofString());
        String jsonTasksRequestAfterDelete = tasksResponseAfterDelete.body();
        HashMap<Integer, Task> allTasksAfterDelete = gson.fromJson(jsonTasksRequestAfterDelete,
                new TypeToken<HashMap<Integer, Task>>() {
                }.getType());

        assertEquals(200, task2ResponseDelete.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(1, allTasksAfterDelete.size(), "Количество задач длжно быть: 1");

        HttpRequest allTasksRequestToDelete = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> allTasksResponseDelete = client.send(allTasksRequestToDelete,
                HttpResponse.BodyHandlers.ofString());
        HttpRequest tasksRequestAfterDeleteAll = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> tasksResponseAfterDeleteAll = client.send(tasksRequestAfterDeleteAll,
                HttpResponse.BodyHandlers.ofString());
        String jsonTasksRequestAfterDeleteAll = tasksResponseAfterDeleteAll.body();

        assertEquals(200, allTasksResponseDelete.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals("No tasks found", jsonTasksRequestAfterDeleteAll,
                "Задачи не должны быть найдены, так как они удалены и список пуст");
    }

    @Test
    void createGetAndDeleteEpicAndSubtasks() throws IOException, InterruptedException {
        start();

        URI url = URI.create("http://localhost:8090/tasks/epic/");
        URI url1 = URI.create("http://localhost:8090/tasks/epic/?id=1");
        URI url2 = URI.create("http://localhost:8090/tasks/epic/?id=4");
        URI url3 = URI.create("http://localhost:8090/tasks/subtask/");
        URI url4 = URI.create("http://localhost:8090/tasks/subtask/?id=2");
        URI url5 = URI.create("http://localhost:8090/tasks/subtask/?id=3");

        Epic epic1 = new Epic(1, TaskTypes.EPIC, "Эпик 1", "Описание эпика 1",
                LocalDateTime.of(2022, 5, 31, 7, 0), 0);
        Subtask subtask1 = new Subtask(2, TaskTypes.SUBTASK, "Сабтаск 1", "Описание сабтаска 1", Status.NEW,
                1, LocalDateTime.of(2022, 5, 31, 8, 0), 20);
        Subtask subtask2 = new Subtask(3, TaskTypes.SUBTASK, "Сабтаск 2", "Описание сабтаска 2", Status.NEW,
                1, LocalDateTime.of(2022, 5, 31, 9, 0), 20);
        Epic epic2 = new Epic(4, TaskTypes.EPIC, "Эпик 2", "Описание эпика 2",
                LocalDateTime.of(2022, 6, 1, 7, 0), 0);
        gson = getGson();
        String jsonEpic1 = gson.toJson(epic1);
        String jsonSubtask1 = gson.toJson(subtask1);
        String jsonSubtask2 = gson.toJson(subtask2);
        String jsonEpic2 = gson.toJson(epic2);

        final HttpRequest.BodyPublisher bodyEpic1 = HttpRequest.BodyPublishers.ofString(jsonEpic1);
        HttpRequest requestEpic1 = HttpRequest.newBuilder().uri(url).POST(bodyEpic1).build();
        HttpResponse<String> responseEpic1 = client.send(requestEpic1, HttpResponse.BodyHandlers.ofString());

        HttpRequest getEpic1Request = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> getEpic1Response = client.send(getEpic1Request, HttpResponse.BodyHandlers.ofString());
        String epic1ToCheckBody = getEpic1Response.body();
        Epic epic1ToCheck = gson.fromJson(epic1ToCheckBody, Epic.class);


        assertEquals(200, responseEpic1.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(1, epic1ToCheck.getId(), "Id epic1ToCheck1 должен быть: 1");

        final HttpRequest.BodyPublisher bodySubtask1 = HttpRequest.BodyPublishers.ofString(jsonSubtask1);
        HttpRequest requestSubtask1 = HttpRequest.newBuilder().uri(url3).POST(bodySubtask1).build();
        HttpResponse<String> responseSubtask1 = client.send(requestSubtask1, HttpResponse.BodyHandlers.ofString());

        HttpRequest getSubtask1Request = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> getSubtask1Response = client.send(getSubtask1Request, HttpResponse.BodyHandlers.ofString());
        String subtask1ToCheckBody = getSubtask1Response.body();
        Subtask subtask1ToCheck = gson.fromJson(subtask1ToCheckBody, Subtask.class);

        HttpRequest getEpic1Request2 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> getEpic1Response2 = client.send(getEpic1Request2, HttpResponse.BodyHandlers.ofString());
        String epic1ToCheckBody2 = getEpic1Response2.body();
        Epic epic1ToCheckUpdated = gson.fromJson(epic1ToCheckBody2, Epic.class);

        assertEquals(200, responseSubtask1.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(2, subtask1ToCheck.getId(), "Id subtask1ToCheck должен быть: 2");
        assertEquals(1, subtask1ToCheck.getEpicID(), "EpicId subtask1ToCheck должен быть: 1");
        assertEquals(1, epic1ToCheckUpdated.getSubtasks().size(), "Количество сабтасков должно быть: 1");

        final HttpRequest.BodyPublisher bodySubtask2 = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        HttpRequest requestSubtask2 = HttpRequest.newBuilder().uri(url3).POST(bodySubtask2).build();
        HttpResponse<String> responseSubtask2 = client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());

        HttpRequest getSubtask2Request = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> getSubtask2Response = client.send(getSubtask2Request, HttpResponse.BodyHandlers.ofString());
        String subtask2ToCheckBody = getSubtask2Response.body();
        Subtask subtask2ToCheck = gson.fromJson(subtask2ToCheckBody, Subtask.class);

        HttpRequest getEpic1Request3 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> getEpic1Response3 = client.send(getEpic1Request3, HttpResponse.BodyHandlers.ofString());
        String epic1ToCheckBody3 = getEpic1Response3.body();
        Epic epic1ToCheckUpdated2 = gson.fromJson(epic1ToCheckBody3, Epic.class);

        assertEquals(200, responseSubtask2.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(3, subtask2ToCheck.getId(), "Id subtask1ToCheck должен быть: 3");
        assertEquals(1, subtask2ToCheck.getEpicID(), "EpicId subtask1ToCheck должен быть: 1");
        assertEquals(2, epic1ToCheckUpdated2.getSubtasks().size(),
                "Количество сабтасков должно быть: 2");
        assertEquals(subtask1ToCheck.getStart(), epic1ToCheckUpdated2.getStart(),
                "Время начала эпика 1 и начала сабтаска 1 должно совпадать");
        assertEquals(subtask2ToCheck.getEnd(), epic1ToCheckUpdated2.getEnd(),
                "Время конца эпика 1 и конца сабтаска 2 должно совпадать");
        assertEquals(40, epic1ToCheckUpdated2.getDuration(),
                "Продолжительность эпика 1 должна быть: 40");

        final HttpRequest.BodyPublisher bodyEpic2 = HttpRequest.BodyPublishers.ofString(jsonEpic2);
        HttpRequest requestEpic2 = HttpRequest.newBuilder().uri(url).POST(bodyEpic2).build();
        HttpResponse<String> responseEpic2 = client.send(requestEpic2, HttpResponse.BodyHandlers.ofString());

        HttpRequest getEpic2Request = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> getEpic2Response = client.send(getEpic2Request, HttpResponse.BodyHandlers.ofString());
        String epic2ToCheckBody = getEpic2Response.body();
        Epic epic2ToCheck = gson.fromJson(epic2ToCheckBody, Epic.class);

        assertEquals(200, responseEpic2.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(4, epic2ToCheck.getId(), "Id epic1ToCheck1 должен быть: 4");

        HttpRequest epicsRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> epicsResponse = client.send(epicsRequest, HttpResponse.BodyHandlers.ofString());
        String jsonEpics = epicsResponse.body();
        HashMap<Integer, Epic> allEpics = gson.fromJson(jsonEpics, new TypeToken<HashMap<Integer, Epic>>() {
        }.getType());

        assertEquals(200, epicsResponse.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(2, allEpics.size(), "Количество эпиков длжно быть: 2");

        HttpRequest epic2RequestToDelete = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> epic2ResponseDelete = client.send(epic2RequestToDelete, HttpResponse.BodyHandlers.ofString());
        HttpRequest epicsRequestAfterDelete = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> epicsResponseAfterDelete = client.send(epicsRequestAfterDelete,
                HttpResponse.BodyHandlers.ofString());
        String jsonEpicsRequestAfterDelete = epicsResponseAfterDelete.body();
        HashMap<Integer, Epic> allEpicsAfterDelete = gson.fromJson(jsonEpicsRequestAfterDelete,
                new TypeToken<HashMap<Integer, Epic>>() {
                }.getType());

        assertEquals(200, epic2ResponseDelete.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(1, allEpicsAfterDelete.size(), "Количество эпиков должо быть: 1");

        HttpRequest subtask2RequestToDelete = HttpRequest.newBuilder().uri(url5).DELETE().build();
        HttpResponse<String> subtask2ResponseDelete = client.send(subtask2RequestToDelete,
                HttpResponse.BodyHandlers.ofString());
        HttpRequest subtasksRequestAfterDelete = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> subtasksResponseAfterDelete = client.send(subtasksRequestAfterDelete,
                HttpResponse.BodyHandlers.ofString());
        String jsonSubtasksResponseAfterDelete = subtasksResponseAfterDelete.body();
        HashMap<Integer, Subtask> allSubtasksAfterDelete = gson.fromJson(jsonSubtasksResponseAfterDelete,
                new TypeToken<HashMap<Integer, Subtask>>() {
                }.getType());

        HttpRequest getEpic1Request4 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> getEpic1Response4 = client.send(getEpic1Request4, HttpResponse.BodyHandlers.ofString());
        String epic1ToCheckBody4 = getEpic1Response4.body();
        Epic epic1ToCheckUpdated3 = gson.fromJson(epic1ToCheckBody4, Epic.class);

        assertEquals(200, subtask2ResponseDelete.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals(1, allSubtasksAfterDelete.size(), "Количество сабтасков должо быть: 1");
        assertEquals(1, epic1ToCheckUpdated3.getSubtasks().size(),
                "Количество сабтасков эпика 1 должо быть: 1");

        HttpRequest subtasksRequestToDelete = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> subtasksResponseDelete = client.send(subtasksRequestToDelete,
                HttpResponse.BodyHandlers.ofString());
        HttpRequest subtasksRequestAfterDeleteAll = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> subtasksResponseAfterDeleteAll = client.send(subtasksRequestAfterDeleteAll,
                HttpResponse.BodyHandlers.ofString());
        String jsonSubtasksResponseAfterDeleteAll = subtasksResponseAfterDeleteAll.body();

        HttpRequest getEpic1Request5 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> getEpic1Response5 = client.send(getEpic1Request5, HttpResponse.BodyHandlers.ofString());
        String epic1ToCheckBody5 = getEpic1Response5.body();
        Epic epic1ToCheckUpdated4 = gson.fromJson(epic1ToCheckBody5, Epic.class);

        assertEquals(200, subtasksResponseDelete.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals("No subtasks found", jsonSubtasksResponseAfterDeleteAll,
                "Подзадачи не должны быть найдены, так как они удалены и список пуст");
        assertEquals(0, epic1ToCheckUpdated4.getSubtasks().size(),
                "Список подзадач эпика 1 должен быть пуст");

        HttpRequest allEpicsRequestToDelete = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> allEpicsResponseDelete = client.send(allEpicsRequestToDelete,
                HttpResponse.BodyHandlers.ofString());
        HttpRequest epicsRequestAfterDeleteAll = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> epicsResponseAfterDeleteAll = client.send(epicsRequestAfterDeleteAll,
                HttpResponse.BodyHandlers.ofString());
        String jsonEpicsRequestAfterDeleteAll = epicsResponseAfterDeleteAll.body();

        assertEquals(200, allEpicsResponseDelete.statusCode(), "Код ответа сервера должен быть: 200");
        assertEquals("No epics found", jsonEpicsRequestAfterDeleteAll,
                "Эпики не должны быть найдены, так как они удалены и список пуст");
    }

    private static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
