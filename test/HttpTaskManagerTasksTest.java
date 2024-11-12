import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private static String jsonResponse = "";
    private static TaskManager managers = Managers.getDefault();
    static Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter()).registerTypeAdapter(Duration.class, new HttpTaskServer.DurationAdapter()).setPrettyPrinting().create();
    private static HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new HttpTaskManagerTasksTest.TaskHandler());
        httpServer.createContext("/subtasks", new HttpTaskManagerTasksTest.SubTasksHandler());
        httpServer.createContext("/epics", new HttpTaskManagerTasksTest.EpicsHandler());
        httpServer.createContext("/history", new HttpTaskManagerTasksTest.HistoryHandler());
        httpServer.createContext("/prioritized", new HttpTaskManagerTasksTest.PrioritizedHandler());

        httpServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpServer.stop(0);
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        assertEquals(201, response.statusCode(), "Код ответа не 201");

        List<Task> tasksFromManager = managers.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            try {
                String method = httpExchange.getRequestMethod();
                URI requestURI = httpExchange.getRequestURI();
                String path = requestURI.getPath();
                String[] pathArray = path.split("/");
                int lengthPath = pathArray.length;

                switch (method) {
                    case "GET":
                        if (lengthPath < 3) {
                            int code = getAllTasks();
                            sendResponseHeaders(httpExchange, code);
                            break;
                        } else {
                            int code = getIdTasks(pathArray[2]);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        }
                    case "POST":
                        if (lengthPath < 3) {
                            int code = createTask(httpExchange);
                            sendResponseHeaders(httpExchange, code);
                        } else {
                            int code = updateTask(pathArray[2], httpExchange);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        }
                        break;
                    case "DELETE":
                        if (lengthPath > 3) {
                            int code = deleteTask(pathArray[2]);
                            sendResponseHeaders(httpExchange, code);
                        }
                        break;
                }
            } catch (Exception e) {
                sendResponseHeaders(httpExchange, 500);
            }
        }
    }

    static class SubTasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String method = httpExchange.getRequestMethod();
                URI requestURI = httpExchange.getRequestURI();
                String path = requestURI.getPath();
                String[] pathArray = path.split("/");
                int lengthPath = pathArray.length;

                switch (method) {
                    case "GET":
                        if (lengthPath < 3) {
                            int code = getAllSubtasks();
                            sendResponseHeaders(httpExchange, code);
                            break;
                        } else {
                            int code = getIdSubtask(pathArray[2]);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        }
                    case "POST":
                        if (lengthPath < 3) {
                            int code = createSubtask(httpExchange);
                            sendResponseHeaders(httpExchange, code);
                        } else {
                            int code = updateSubtask(pathArray[2], httpExchange);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        }
                        break;
                    case "DELETE":
                        if (lengthPath > 3) {
                            int code = deleteSubtaks(pathArray[2]);
                            sendResponseHeaders(httpExchange, code);
                        }
                        break;
                }
            } catch (Exception e) {
                sendResponseHeaders(httpExchange, 500);
            }
        }
    }

    static class EpicsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            try {
                String method = httpExchange.getRequestMethod();
                URI requestURI = httpExchange.getRequestURI();
                String path = requestURI.getPath();
                String[] pathArray = path.split("/");
                int lengthPath = pathArray.length;

                switch (method) {
                    case "GET":
                        if (lengthPath < 3) {
                            int code = getAllEpics();
                            sendResponseHeaders(httpExchange, code);
                            break;
                        } else if (lengthPath == 3) {
                            int code = getIdEpic(pathArray[2]);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        } else if (lengthPath == 4) {
                            if (pathArray[3].equals("subtasks")) {
                                int code = getEpicSubtasksId(pathArray[2]);
                                sendResponseHeaders(httpExchange, code);
                                break;
                            }
                        }
                    case "POST":
                        if (lengthPath < 3) {
                            int code = createEpic(httpExchange);
                            sendResponseHeaders(httpExchange, code);
                        } else {
                            int code = updateEpic(pathArray[2], httpExchange);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        }
                        break;
                    case "DELETE":
                        if (lengthPath > 3) {
                            int code = deleteEpic(pathArray[2]);
                            sendResponseHeaders(httpExchange, code);
                        }
                        break;
                }
            } catch (Exception e) {
                sendResponseHeaders(httpExchange, 500);
            }
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String method = httpExchange.getRequestMethod();

                if (method.equals("GET")) {
                    int code = getHistory();
                    sendResponseHeaders(httpExchange, code);
                }
            } catch (Exception e) {
                sendResponseHeaders(httpExchange, 500);
            }
        }
    }

    static class PrioritizedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                String method = httpExchange.getRequestMethod();

                if (method.equals("GET")) {
                    int code = getPrioritizedTasks();
                    sendResponseHeaders(httpExchange, code);
                }
            } catch (Exception e) {
                sendResponseHeaders(httpExchange, 500);
            }
        }
    }

    private static void sendResponseHeaders(HttpExchange httpExchange, int rCode) throws IOException {
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");

        try (OutputStream os = httpExchange.getResponseBody()) {
            if (rCode == 200 || rCode == 201) {
                httpExchange.sendResponseHeaders(rCode, jsonResponse.getBytes().length);
                os.write(jsonResponse.getBytes());
            } else if (rCode == 404) {
                jsonResponse = gson.toJson("Задача не найдена");
                httpExchange.sendResponseHeaders(rCode, jsonResponse.getBytes().length);
                os.write(jsonResponse.getBytes());
            } else if (rCode == 406) {
                jsonResponse = gson.toJson("Задача пересекается с существующей");
                httpExchange.sendResponseHeaders(rCode, jsonResponse.getBytes().length);
                os.write(jsonResponse.getBytes());
            } else if (rCode == 500) {
                jsonResponse = gson.toJson("Внутренняя ошибка сервера");
                httpExchange.sendResponseHeaders(rCode, jsonResponse.getBytes().length);
                os.write(jsonResponse.getBytes());
            }
        } finally {
            jsonResponse = "";  // Очистка ответа после отправки
        }
    }

    private static int getAllTasks() {
        try {
            List<Task> allTasks = managers.getAllTasks();

            for (Task allTask : allTasks) {
                jsonResponse = jsonResponse + gson.toJson(allTask);
            }
            return 200;
        } catch (NullPointerException e) {
            return 404;
        }
    }

    private static int getIdTasks(String idStr) {
        int id = Integer.parseInt(idStr);
        try {
            Task task = managers.getTaskInId(id);
            jsonResponse = gson.toJson(task);
            return 200;
        } catch (NullPointerException e) {
            return 404;
        }
    }

    private static int createTask(HttpExchange httpExchange) {
        InputStream inputStream = httpExchange.getRequestBody();
        Task task = gson.fromJson(new InputStreamReader(inputStream), Task.class);
        managers.addTask(task);
        jsonResponse = gson.toJson("Добавлена задача");
        return 201;
    }

    private static int updateTask(String idStr, HttpExchange httpExchange) {
        try {
            int id = Integer.parseInt(idStr);
            Task task = managers.getTaskInId(id);
            InputStream inputStream = httpExchange.getRequestBody();
            Task task1 = gson.fromJson(new InputStreamReader(inputStream), Task.class);
            managers.updateTask(task1);
            jsonResponse = gson.toJson("Задача обновлена");
            return 201;
        } catch (NullPointerException e) {
            return 404;
        }
    }

    private static int deleteTask(String idStr) {
        int id = Integer.parseInt(idStr);
        managers.removeTaskInId(id);
        jsonResponse = gson.toJson("Подзадача удалена");
        return 200;
    }

    private static int getAllSubtasks() {
        try {
            List<Subtask> allTasks = managers.getAllsubtasks();

            for (Subtask allTask : allTasks) {
                jsonResponse = jsonResponse + gson.toJson(allTask);
            }
            return 200;
        } catch (NullPointerException e) {
            return 404;
        }

    }

    private static int getIdSubtask(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            Subtask subtask = managers.getSubTaskInId(id);
            jsonResponse = gson.toJson(subtask);
            return 200;
        } catch (NullPointerException e) {
            return 404;
        }

    }

    private static int createSubtask(HttpExchange httpExchange) {
        InputStream inputStream = httpExchange.getRequestBody();
        Subtask subtask = gson.fromJson(new InputStreamReader(inputStream), Subtask.class);
        managers.addSubTask(subtask);
        jsonResponse = gson.toJson("Добавлена подзадача");
        return 201;
    }

    private static int updateSubtask(String idStr, HttpExchange httpExchange) {
        try {
            int id = Integer.parseInt(idStr);
            Subtask subtask = managers.getSubTaskInId(id);
            InputStream inputStream = httpExchange.getRequestBody();
            Subtask subtask1 = gson.fromJson(new InputStreamReader(inputStream), Subtask.class);
            managers.updateTask(subtask1);
            jsonResponse = gson.toJson("Подзадача обновлена");
            return 201;
        } catch (NullPointerException e) {
            return 404;
        }
    }

    private static int deleteSubtaks(String idStr) {
        int id = Integer.parseInt(idStr);
        managers.removeSubTaskInId(id);
        jsonResponse = gson.toJson("Подзадача удалена");
        return 200;
    }

    private static int getAllEpics() {
        try {
            List<Epic> allTasks = managers.getAllEpics();

            for (Epic allTask : allTasks) {
                jsonResponse = jsonResponse + gson.toJson(allTask);
            }
            return 200;
        } catch (NullPointerException e) {
            return 404;
        }
    }

    private static int getIdEpic(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            Epic epic = managers.getEpicInId(id);
            jsonResponse = gson.toJson(epic);
            return 200;
        } catch (NullPointerException e) {
            return 404;
        }
    }

    private static int getEpicSubtasksId(String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            Epic epic = managers.getEpicInId(id);
            List<Subtask> subtasks = managers.getsubtasksInEpic(id);
            for (Subtask subtask : subtasks) {
                jsonResponse = gson.toJson(subtask);
            }
            return 200;
        } catch (NullPointerException e) {
            return 404;
        }

    }

    private static int createEpic(HttpExchange httpExchange) {
        InputStream inputStream = httpExchange.getRequestBody();
        Epic epic = gson.fromJson(new InputStreamReader(inputStream), Epic.class);
        managers.addEpic(epic);
        jsonResponse = gson.toJson("Добавлен эпик");
        return 201;
    }

    private static int updateEpic(String idStr, HttpExchange httpExchange) {
        try {
            Integer id = Integer.parseInt(idStr);
            Epic epic = managers.getEpicInId(id);
            InputStream inputStream = httpExchange.getRequestBody();
            Epic epic1 = gson.fromJson(new InputStreamReader(inputStream), Epic.class);
            managers.updateTask(epic1);
            jsonResponse = gson.toJson("Эпик обновлена");
            return 201;

        } catch (NullPointerException e) {
            return 404;
        }

    }

    private static int deleteEpic(String idStr) {
        int id = Integer.parseInt(idStr);
        managers.removeEpicInId(id);
        jsonResponse = gson.toJson("Эпик удален");
        return 200;
    }

    private static int getHistory() {
        try {
            List<Task> tasks = managers.getHistory();
            for (Task task : tasks) {
                jsonResponse = jsonResponse + gson.toJson(task);
            }
            return 200;
        } catch (NullPointerException e) {
            return 401;
        }
    }

    private static int getPrioritizedTasks() {
        try {
            List<Task> tasks = managers.getPrioritizedTasks();
            for (Task task : tasks) {
                jsonResponse = jsonResponse + gson.toJson(task);
            }
            return 200;
        } catch (NullPointerException e) {
            return 401;
        }
    }

    public static class DurationAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
            jsonWriter.value(duration.toString());
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException {
            return Duration.parse(jsonReader.nextString());
        }
    }

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            jsonWriter.value(localDateTime.toString());
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString());
        }
    }

    //Методы для тестов
    public static Gson getGson() {
        return new Gson();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

        // Пример обработки запросов для /tasks
        httpServer.createContext("/tasks", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("POST".equals(exchange.getRequestMethod())) {
                    InputStream inputStream = exchange.getRequestBody();
                    Task task = new Gson().fromJson(new InputStreamReader(inputStream), Task.class);
                    managers.addTask(task);
                    exchange.sendResponseHeaders(201, 0);
                    exchange.close();
                }
            }
        });
        httpServer.start();
        System.out.println("Server started on port 8080");
    }

    public void stop() {
        httpServer.stop(0);
    }
}
