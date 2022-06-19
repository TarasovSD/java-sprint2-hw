package manager.HTMLController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

public class HttpTaskServer {
    private static final int PORT = 8090;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson;
    private final TaskManager manager;
    public final HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        gson = getGson();
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks/task/", this::taskHandler);
        httpServer.createContext("/tasks/subtask/", this::subtaskHandler);
        httpServer.createContext("/tasks/epic/", this::epicHandler);
        httpServer.createContext("/tasks/", this::prioritizedTasksHandler);
        httpServer.createContext("/tasks/history/", this::historyHandler);
        manager = Managers.getDefault();
    }

    private void prioritizedTasksHandler(HttpExchange exchange) {
        TreeSet<Task> prioritizedTasks = manager.getPrioritizedTasks();
        try (OutputStream os = exchange.getResponseBody()) {
            if (!prioritizedTasks.isEmpty()) {
                exchange.sendResponseHeaders(200, 0);
                os.write(gson.toJson(prioritizedTasks).getBytes());
            } else {
                exchange.sendResponseHeaders(404, 0);
                os.write("No tasks found".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void historyHandler(HttpExchange exchange) {
        List<Task> history = manager.getHistory();
        try (OutputStream os = exchange.getResponseBody()) {
            if (!history.isEmpty()) {
                exchange.sendResponseHeaders(200, 0);
                os.write(gson.toJson(history).getBytes());
            } else {
                exchange.sendResponseHeaders(404, 0);
                os.write("No tasks found in history".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void epicHandler(HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    Integer epicId = getQueryParamId(exchange);
                    if (epicId != null) {
                        handleGetEpicWithId(epicId, exchange, os);
                    } else {
                        handleGetAllEpics(exchange, os);
                    }
                    break;
                }
                case "DELETE": {
                    Integer epicId = getQueryParamId(exchange);
                    if (epicId != null) {
                        handleDeleteEpicWithId(epicId, exchange, os);
                    } else {
                        handleDeleteAllEpics(exchange, os);
                    }
                    break;
                }
                case "POST": {
                    handlePutEpicWithId(exchange, os);
                    break;
                }
            }
        }
    }

    private void handlePutEpicWithId(HttpExchange exchange, OutputStream os) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Epic epicToPost = gson.fromJson(body, Epic.class);
        Integer epicID = epicToPost.getId();
        if (epicToPost != null) {
            Epic epic = manager.getEpic(epicID);
            if (epic != null) {

                manager.updateEpic(epicToPost);
                exchange.sendResponseHeaders(200, 0);
                String out = "Epic with id " + epicID + " updated";
                os.write(out.getBytes());
            } else {
                manager.createEpic(epicToPost);
                epicToPost.setSubtasks(new ArrayList<>());
                exchange.sendResponseHeaders(200, 0);
                String out = "Epic with id " + epicID + " created";
                os.write(out.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("Epic not created".getBytes());
        }
    }

    private void handleDeleteAllEpics(HttpExchange exchange, OutputStream os) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        if (!epics.isEmpty()) {
            manager.deleteAllEpics();
            List<Epic> epicsAfterRemoval = manager.getAllEpics();
            if (epicsAfterRemoval.isEmpty()) {
                exchange.sendResponseHeaders(200, 0);
                os.write("Epics removed".getBytes());
            } else {
                exchange.sendResponseHeaders(404, 0);
                os.write("Epics not removed".getBytes());
            }
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No epics to delete".getBytes());
        }
    }

    private void handleDeleteEpicWithId(Integer epicId, HttpExchange exchange, OutputStream os) throws IOException {
        Epic epic = manager.getEpic(epicId);
        if (epic == null) {
            exchange.sendResponseHeaders(404, 0);
            os.write("Epic not found".getBytes());
            return;
        }
        manager.deleteEpic(epicId);
        Epic epicToDelete = manager.getEpic(epicId);
        if (epicToDelete == null) {
            exchange.sendResponseHeaders(200, 0);
            os.write("Epic deleted".getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("Epic not deleted".getBytes());
        }
    }

    private void handleGetAllEpics(HttpExchange exchange, OutputStream os) throws IOException {
        List<Epic> epics = manager.getAllEpics();
        if (!epics.isEmpty()) {
            exchange.sendResponseHeaders(200, 0);
            os.write(gson.toJson(epics).getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No epics found".getBytes());
        }
    }

    private void handleGetEpicWithId(Integer epicId, HttpExchange exchange, OutputStream os) throws IOException {
        Epic epic = manager.getEpic(epicId);
        if (epic != null) {
            exchange.sendResponseHeaders(200, 0);
            os.write(gson.toJson(epic).getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No epic created".getBytes());
        }
    }

    private void subtaskHandler(HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    Integer subtaskId = getQueryParamId(exchange);
                    if (subtaskId != null) {
                        handleGetSubtaskWithId(subtaskId, exchange, os);
                    } else {
                        handleGetAllSubtasks(exchange, os);
                    }
                    break;
                }
                case "DELETE": {
                    Integer subtaskId = getQueryParamId(exchange);
                    if (subtaskId != null) {
                        handleDeleteSubtaskWithId(subtaskId, exchange, os);
                    } else {
                        handleDeleteAllSubtasks(exchange, os);
                    }
                    break;
                }
                case "POST": {
                    handlePutSubtaskWithId(exchange, os);
                    break;
                }
            }
        }
    }

    private void handlePutSubtaskWithId(HttpExchange exchange, OutputStream os) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Subtask subtaskToPost = gson.fromJson(body, Subtask.class);
        Integer subtaskID = subtaskToPost.getId();
        if (subtaskToPost != null) {
            if (manager.getSubtask(subtaskID) != null) {
                manager.updateSubtask(subtaskToPost);
                exchange.sendResponseHeaders(200, 0);
                String out = "Subtask with id " + subtaskID + " updated";
                os.write(out.getBytes());
            } else {
                manager.createSubtask(subtaskToPost);
                exchange.sendResponseHeaders(200, 0);
                String out = "Subtask with id " + subtaskID + " created";
                os.write(out.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("Subtask not created".getBytes());
        }
    }

    private void handleDeleteAllSubtasks(HttpExchange exchange, OutputStream os) throws IOException {
        List<Subtask> subtasks = manager.getAllSubtasks();
        if (!subtasks.isEmpty()) {
            manager.deleteAllSubtasks();
            List<Subtask> subtasksAfterRemoval = manager.getAllSubtasks();
            if (subtasksAfterRemoval.isEmpty()) {
                exchange.sendResponseHeaders(200, 0);
                os.write("Subtasks removed".getBytes());
            } else {
                exchange.sendResponseHeaders(404, 0);
                os.write("Subtasks not removed".getBytes());
            }
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No subtasks to delete".getBytes());
        }
    }

    private void handleDeleteSubtaskWithId(Integer subtaskId, HttpExchange exchange, OutputStream os) throws IOException {
        Subtask subtask = manager.getSubtask(subtaskId);
        if (subtask == null) {
            exchange.sendResponseHeaders(404, 0);
            os.write("Subtask not found".getBytes());
            return;
        }
        manager.deleteSubtask(subtaskId);
        Subtask subtaskToDelete = manager.getSubtask(subtaskId);
        if (subtaskToDelete == null) {
            exchange.sendResponseHeaders(200, 0);
            os.write("Subtask deleted".getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("Subtask not deleted".getBytes());
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange, OutputStream os) throws IOException {
        List<Subtask> subtasks = manager.getAllSubtasks();
        if (!subtasks.isEmpty()) {
            exchange.sendResponseHeaders(200, 0);
            os.write(gson.toJson(subtasks).getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No subtasks found".getBytes());
        }
    }

    private void handleGetSubtaskWithId(Integer subtaskId, HttpExchange exchange, OutputStream os) throws IOException {
        Subtask subtask = manager.getSubtask(subtaskId);
        if (subtask != null) {
            exchange.sendResponseHeaders(200, 0);
            os.write(gson.toJson(subtask).getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No subtask created".getBytes());
        }
    }

    private void taskHandler(HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    Integer taskId = getQueryParamId(exchange);
                    if (taskId != null) {
                        handleGetTaskWithId(taskId, exchange, os);
                    } else {
                        handleGetAllTasks(exchange, os);
                    }
                    break;
                }
                case "DELETE": {
                    Integer taskId = getQueryParamId(exchange);
                    if (taskId != null) {
                        handleDeleteTaskWithId(taskId, exchange, os);
                    } else {
                        handleDeleteAllTasks(exchange, os);
                    }
                    break;
                }
                case "POST": {
                    handlePutTaskWithId(exchange, os);
                    break;
                }
            }
        }
    }

    private void handlePutTaskWithId(HttpExchange exchange, OutputStream os) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        Task taskToPost = gson.fromJson(body, Task.class);
        Integer taskID = taskToPost.getId();
        if (taskToPost != null) {
            if (manager.getTask(taskID) != null) {
                manager.updateTask(taskToPost);
                exchange.sendResponseHeaders(200, 0);
                String out = "Task with id " + taskID + " updated";
                os.write(out.getBytes());
            } else {
                manager.createTask(taskToPost);
                exchange.sendResponseHeaders(200, 0);
                String out = "Task with id " + taskID + " created";
                os.write(out.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("Task not created".getBytes());
        }
    }

    private void handleDeleteAllTasks(HttpExchange exchange, OutputStream os) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        if (!tasks.isEmpty()) {
            manager.deleteAllTasks();
            List<Task> tasksAfterRemoval = manager.getAllTasks();
            if (tasksAfterRemoval.isEmpty()) {
                exchange.sendResponseHeaders(200, 0);
                os.write("Tasks removed".getBytes());
            } else {
                exchange.sendResponseHeaders(404, 0);
                os.write("Tasks not removed".getBytes());
            }
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No tasks to delete".getBytes());
        }
    }

    private void handleDeleteTaskWithId(Integer taskId, HttpExchange exchange, OutputStream os) throws IOException {
        Task task = manager.getTask(taskId);
        if (task == null) {
            exchange.sendResponseHeaders(404, 0);
            os.write("Task not found".getBytes());
            return;
        }
        manager.deleteTask(taskId);
        Task taskToDelete = manager.getTask(taskId);
        if (taskToDelete == null) {
            exchange.sendResponseHeaders(200, 0);
            os.write("Task deleted".getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("Task not deleted".getBytes());
        }
    }

    private void handleGetAllTasks(HttpExchange exchange, OutputStream os) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        if (!tasks.isEmpty()) {
            exchange.sendResponseHeaders(200, 0);
            os.write(gson.toJson(tasks).getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No tasks found".getBytes());
        }
    }

    private void handleGetTaskWithId(Integer taskId, HttpExchange exchange, OutputStream os) throws IOException {
        Task task = manager.getTask(taskId);
        if (task != null) {
            exchange.sendResponseHeaders(200, 0);
            os.write(gson.toJson(task).getBytes());
        } else {
            exchange.sendResponseHeaders(404, 0);
            os.write("No task created".getBytes());
        }
    }

    private Integer getQueryParamId(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.contains("id")) {
            String[] paramId = query.split("=");
            if (paramId.length == 2) {
                return Integer.parseInt(paramId[1]);
            }
        }
        return null;
    }

    private static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpServer = new HttpTaskServer();
        httpServer.start();
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
