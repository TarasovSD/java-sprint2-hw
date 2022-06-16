package manager.HTMLController;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import models.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8090;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new Gson();
    private final TaskManager manager;
    public final HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks/task/", this::taskHandler);
        httpServer.createContext("/tasks/subtask/", this::subtaskHandler);
        httpServer.createContext("/tasks/epic/", this::epicHandler);
        httpServer.createContext("/tasks/", this::historyHandler);
        manager = Managers.getDefault();
    }

    private void historyHandler(HttpExchange exchange) {
    }

    private void epicHandler(HttpExchange exchange) {
    }

    private void subtaskHandler(HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            switch (exchange.getRequestMethod()) {
                case "GET": {
                    Integer taskId = getQueryParamId(exchange);
                    exchange.sendResponseHeaders(200, 0);
                    os.write("This is GET for /tasks/subtask/".getBytes(DEFAULT_CHARSET));
                }
            }
            exchange.sendResponseHeaders(200, 0);
            os.write("This is GET for /tasks/subtask/".getBytes(DEFAULT_CHARSET));
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
//                        handleDeleteTaskWithId();
                    } else {
//                        deleteAllTasks();
                    }
                    exchange.sendResponseHeaders(200, 0);
                    os.write("This is DELETE for /tasks/task/".getBytes(DEFAULT_CHARSET));
                    break;
                }
                case "PUT": {
                    Integer taskId = getQueryParamId(exchange);
                    if (taskId != null) {
//                        handlePutTaskWithId();
                    }
                }
            }
        }
    }

    private void handleGetAllTasks(HttpExchange exchange, OutputStream os) throws IOException {
        List<Task> tasks = manager.getAllTasks();
        HashMap<Integer, Task> allTasks = new HashMap<>();
        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                allTasks.put(task.getId(), task);
                exchange.sendResponseHeaders(200, 0);
                os.write(gson.toJson(allTasks).getBytes());
            }
            return;
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
        if (query.contains("id")) {
            String[] paramId = query.split("=");
            if (paramId.length == 2) {
                return  Integer.parseInt(paramId[1]);
            }
        }
        return null;
    }

    private void handleTask(HttpExchange exchange) throws IOException {
            URI requestURI = exchange.getRequestURI();
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            int iD = Integer.parseInt(requestURI.getQuery());
            try (OutputStream os = exchange.getResponseBody()) {
                switch (exchange.getRequestMethod()) {
                    case "GET": {
                        if (iD > 0) {
                            Task task = manager.getTask(iD);
                            if (task != null) {
                                exchange.sendResponseHeaders(200, 0);
                                os.write(gson.toJson(task).getBytes());
                                return;
                            }
                        } else {
                            List<Task> tasks = manager.getAllTasks();
                            HashMap<Integer, Task> allTasks= new HashMap<>();
                            if (!tasks.isEmpty()) {
                                for (Task task : tasks) {
                                    allTasks.put(task.getId(), task);
                                }
                            }
                            exchange.sendResponseHeaders(200, 0);
                            os.write(gson.toJson(allTasks).getBytes());
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
                                Task taskFromJson = gson.fromJson(body, Task.class);
                                manager.updateTask(taskFromJson);
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

    public void start() {
            System.out.println("Запускаем сервер на порту " + PORT);
            System.out.println("Открой в браузере http://localhost:" + PORT + "/");
            httpServer.start();
        }


    public static void main(String[] args) throws IOException, InterruptedException {
        HttpTaskServer httpServer = new HttpTaskServer();
        httpServer.start();
    }

//    static class TasksHandler implements HttpHandler {
//        @Override
//        public void handle(HttpExchange exchange) throws IOException {
//            URI requestURI = exchange.getRequestURI();
//            String path = requestURI.getPath();
//            String[] splitStrings = path.split("/");
//
//            if (splitStrings.length == 2 && splitStrings[1].equals("tasks")) {
//                handleTasks(exchange);
//            } else if (splitStrings.length == 3 && splitStrings[2].equals("task")) {
//                handleTask(exchange);
//            } else if (splitStrings.length == 3 && splitStrings[2].equals("subtask")) {
//                handleSubtask(exchange);
//            } else if (splitStrings.length == 3 && splitStrings[2].equals("epic")) {
//                handleEpic(exchange);
//            } else if (splitStrings.length == 3 && splitStrings[2].equals("history")) {
//                handleHistory(exchange);
//            }
//        }
//
//        public void start() {
//            System.out.println("Запускаем сервер на порту " + PORT);
//            System.out.println("Открой в браузере http://localhost:" + PORT + "/");
//            httpServer.start();
//        }
//
//        private void handleHistory(HttpExchange exchange) throws IOException {
//            try (OutputStream os = exchange.getResponseBody()) {
//                List<Task> history = manager.getHistory();
//                int size = history.size();
//                int[] massiveHistoryId = new int[size];
//                for (int i = 0; i < history.size(); i++) {
//                    int id = history.get(i).getId();
//                    massiveHistoryId[i] = id;
//                }
//                String jsonHistory = gson.toJson(massiveHistoryId);
//                exchange.sendResponseHeaders(200, 0);
//                os.write(jsonHistory.getBytes());
//            }
//        }
//
//        private void handleEpic(HttpExchange exchange) throws IOException {
//            URI requestURI = exchange.getRequestURI();
//            InputStream inputStream = exchange.getRequestBody();
//            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
//            int iD = Integer.parseInt(requestURI.getQuery());
//            try (OutputStream os = exchange.getResponseBody()) {
//                switch (exchange.getRequestMethod()) {
//                    case "GET": {
//                        if (iD > 0) {
//                            Epic epic = manager.getEpic(iD);
//                            if (epic != null) {
//                                exchange.sendResponseHeaders(200, 0);
//                                os.write(gson.toJson(epic).getBytes());
//                                return;
//                            }
//                            exchange.sendResponseHeaders(404, 0);
//                            exchange.close();
//                        }  else {
//
//                        }
//                    }
//                    case "DELETE": {
//                        Epic epic = manager.getEpic(iD);
//                        if (epic != null) {
//                            manager.deleteEpic(iD);
//                            exchange.sendResponseHeaders(200, 0);
//                            return;
//                        }
//                        exchange.sendResponseHeaders(404, 0);
//                        exchange.close();
//                    }
//                    case "POST": {
//                        for (Epic epic : manager.getAllEpics()) {
//                            if (epic.getId() == iD) {
//                                manager.updateEpic(epic);
//                                exchange.sendResponseHeaders(200, 0);
//                                return;
//                            }
//                        }
//                        Epic epic = gson.fromJson(body, Epic.class);
//                        if (epic != null) {
//                            manager.createTask(epic);
//                            exchange.sendResponseHeaders(200, 0);
//                            return;
//                        }
//                        exchange.sendResponseHeaders(404, 0);
//                        exchange.close();
//                    }
//                }
//            }
//        }
//
//        private void handleSubtask(HttpExchange exchange) throws IOException {
//            URI requestURI = exchange.getRequestURI();
//            InputStream inputStream = exchange.getRequestBody();
//            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
//            int iD = Integer.parseInt(requestURI.getQuery());
//            try (OutputStream os = exchange.getResponseBody()) {
//                switch (exchange.getRequestMethod()) {
//                    case "GET": {
//                        Subtask subtask = manager.getSubtask(iD);
//                        if (subtask != null) {
//                            exchange.sendResponseHeaders(200, 0);
//                            os.write(gson.toJson(subtask).getBytes());
//                            return;
//                        }
//                        exchange.sendResponseHeaders(404, 0);
//                        exchange.close();
//                    }
//                    case "DELETE": {
//                        Subtask subtask = manager.getSubtask(iD);
//                        if (subtask != null) {
//                            manager.deleteSubtask(iD);
//                            exchange.sendResponseHeaders(200, 0);
//                            return;
//                        }
//                        exchange.sendResponseHeaders(404, 0);
//                        exchange.close();
//                    }
//                    case "POST": {
//                        for (Subtask subtask : manager.getAllSubtasks()) {
//                            if (subtask.getId() == iD) {
//                                manager.updateSubtask(subtask);
//                                exchange.sendResponseHeaders(200, 0);
//                                return;
//                            }
//                        }
//                        Subtask subtask = gson.fromJson(body, Subtask.class);
//                        if (subtask != null) {
//                            manager.createSubtask(subtask);
//                            exchange.sendResponseHeaders(200, 0);
//                            return;
//                        }
//                        exchange.sendResponseHeaders(404, 0);
//                        exchange.close();
//                    }
//                }
//            }
//        }
//
//        private void handleTask(HttpExchange exchange) throws IOException {
//            URI requestURI = exchange.getRequestURI();
//            InputStream inputStream = exchange.getRequestBody();
//            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
//            int iD = Integer.parseInt(requestURI.getQuery());
//            try (OutputStream os = exchange.getResponseBody()) {
//                switch (exchange.getRequestMethod()) {
//                    case "GET": {
//                        if (iD > 0) {
//                            Task task = manager.getTask(iD);
//                            if (task != null) {
//                                exchange.sendResponseHeaders(200, 0);
//                                os.write(gson.toJson(task).getBytes());
//                                return;
//                            }
//                        } else {
//                            List<Task> tasks = manager.getAllTasks();
//                            HashMap<Integer, Task> allTasks= new HashMap<>();
//                            if (!tasks.isEmpty()) {
//                                for (Task task : tasks) {
//                                    allTasks.put(task.getId(), task);
//                                }
//                            }
//                            exchange.sendResponseHeaders(200, 0);
//                            os.write(gson.toJson(allTasks).getBytes());
//                            return;
//                        }
//                        exchange.sendResponseHeaders(404, 0);
//                        exchange.close();
//                    }
//                    case "DELETE": {
//                        Task task = manager.getTask(iD);
//                        if (task != null) {
//                            manager.deleteTask(iD);
//                            exchange.sendResponseHeaders(200, 0);
//                            return;
//                        }
//                        exchange.sendResponseHeaders(404, 0);
//                        exchange.close();
//                    }
//                    case "POST": {
//                        for (Task task : manager.getAllTasks()) {
//                            if (task.getId() == iD) {
//                                manager.updateTask(task);
//                                exchange.sendResponseHeaders(200, 0);
//                                return;
//                            }
//                        }
//                        Task task = gson.fromJson(body, Task.class);
//                        if (task != null) {
//                            manager.createTask(task);
//                            exchange.sendResponseHeaders(200, 0);
//                            return;
//                        }
//                        exchange.sendResponseHeaders(404, 0);
//                        exchange.close();
//                    }
//                }
//            }
//        }
//
//        private void handleTasks(HttpExchange exchange) throws IOException {
//            try (OutputStream os = exchange.getResponseBody()) {
//                List<Task> tasks = manager.getAllTasks();
//                exchange.sendResponseHeaders(200, 0);
//                os.write(gson.toJson(tasks).getBytes());
//            }
//        }
//    }



}
