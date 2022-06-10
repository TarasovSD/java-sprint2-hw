package manager.HTMLController;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
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
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8090;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();
    private static final TaskManager manager;

    static {
        try {
            manager = Managers.getDefault();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
    }

    static class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitStrings = path.split("/");

            if (splitStrings.length == 2 && splitStrings[1].equals("tasks")) {
                handleTasks(exchange);
            } else if (splitStrings.length == 3 && splitStrings[2].equals("task")) {
                handleTask(exchange);
            } else if (splitStrings.length == 3 && splitStrings[2].equals("subtask")) {
                handleSubtask(exchange);
            } else if (splitStrings.length == 3 && splitStrings[2].equals("epic")) {
                handleEpic(exchange);
            } else if (splitStrings.length == 3 && splitStrings[2].equals("history")) {
                handleHistory(exchange);
            }
        }

        private void handleHistory(HttpExchange exchange) throws IOException {
            try (OutputStream os = exchange.getResponseBody()) {
                List<Task> history = manager.getHistory();
                exchange.sendResponseHeaders(200, 0);
                os.write(gson.toJson(history).getBytes());
            }
        }

        private void handleEpic(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            int iD = Integer.parseInt(requestURI.getQuery());
            try (OutputStream os = exchange.getResponseBody()) {
                switch (exchange.getRequestMethod()) {
                    case "GET": {
                        Epic epic = manager.getEpic(iD);
                        if (epic != null) {
                            exchange.sendResponseHeaders(200, 0);
                            os.write(gson.toJson(epic).getBytes());
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                    case "DELETE": {
                        Epic epic = manager.getEpic(iD);
                        if (epic != null) {
                            manager.deleteEpic(iD);
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                    case "POST": {
                        for (Epic epic : manager.getAllEpics()) {
                            if (epic.getId() == iD) {
                                manager.updateEpic(epic);
                                exchange.sendResponseHeaders(200, 0);
                                return;
                            }
                        }
                        Epic epic = gson.fromJson(body, Epic.class);
                        if (epic != null) {
                            manager.createTask(epic);
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                }
            }
        }

        private void handleSubtask(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            int iD = Integer.parseInt(requestURI.getQuery());
            try (OutputStream os = exchange.getResponseBody()) {
                switch (exchange.getRequestMethod()) {
                    case "GET": {
                        Subtask subtask = manager.getSubtask(iD);
                        if (subtask != null) {
                            exchange.sendResponseHeaders(200, 0);
                            os.write(gson.toJson(subtask).getBytes());
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                    case "DELETE": {
                        Subtask subtask = manager.getSubtask(iD);
                        if (subtask != null) {
                            manager.deleteSubtask(iD);
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                    case "POST": {
                        for (Subtask subtask : manager.getAllSubtasks()) {
                            if (subtask.getId() == iD) {
                                manager.updateSubtask(subtask);
                                exchange.sendResponseHeaders(200, 0);
                                return;
                            }
                        }
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        if (subtask != null) {
                            manager.createSubtask(subtask);
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                }
            }
        }

        private void handleTask(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            int iD = Integer.parseInt(requestURI.getQuery());
            try (OutputStream os = exchange.getResponseBody()) {
                switch (exchange.getRequestMethod()) {
                    case "GET": {
                        Task task = manager.getTask(iD);
                        if (task != null) {
                            exchange.sendResponseHeaders(200, 0);
                            os.write(gson.toJson(task).getBytes());
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                    case "DELETE": {
                        Task task = manager.getTask(iD);
                        if (task != null) {
                            manager.deleteTask(iD);
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                    case "POST": {
                        for (Task task : manager.getAllTasks()) {
                            if (task.getId() == iD) {
                                manager.updateTask(task);
                                exchange.sendResponseHeaders(200, 0);
                                return;
                            }
                        }
                        Task task = gson.fromJson(body, Task.class);
                        if (task != null) {
                            manager.createTask(task);
                            exchange.sendResponseHeaders(200, 0);
                            return;
                        }
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                    }
                }
            }
        }

        private void handleTasks(HttpExchange exchange) throws IOException {
            try (OutputStream os = exchange.getResponseBody()) {
                List<Task> tasks = manager.getAllTasks();
                exchange.sendResponseHeaders(200, 0);
                os.write(gson.toJson(tasks).getBytes());
            }
        }
    }

}
