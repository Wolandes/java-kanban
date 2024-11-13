package manager;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class HttpTaskServer {

    private static TaskManager managers = Managers.getDefault();
    private static Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).registerTypeAdapter(Duration.class, new DurationAdapter()).setPrettyPrinting().create();
    private static String jsonResponse = "";

    public static void main(String[] args) throws IOException {
        HttpServerInitializer httpServerWork = new HttpServerInitializer();
        httpServerWork.start();
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
                        Task task = getFromBodyTask(httpExchange);
                        if (task.getId() == 0) {
                            int code = createTask(task);
                            sendResponseHeaders(httpExchange, code);
                        } else {
                            int code = updateTask(task);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        }
                        break;
                    case "DELETE":
                        if (lengthPath == 3) {
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
                        Subtask subtask = getFromBodySubtask(httpExchange);
                        if (subtask.getId() == 0) {
                            int code = createSubtask(subtask);
                            sendResponseHeaders(httpExchange, code);
                        } else {
                            int code = updateSubtask(subtask);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        }
                        break;
                    case "DELETE":
                        if (lengthPath == 3) {
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
                        Epic epic = getFromBodyEpic(httpExchange);
                        if (epic.getId() == 0) {
                            int code = createEpic(epic);
                            sendResponseHeaders(httpExchange, code);
                        } else {
                            int code = updateEpic(epic);
                            sendResponseHeaders(httpExchange, code);
                            break;
                        }
                        break;
                    case "DELETE":
                        if (lengthPath == 3) {
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

    private static int createTask(Task task) {
        managers.addTask(task);
        List<Task> lastTask = managers.getAllTasks();
        int id = 0;
        for (Task task1 : lastTask) {
            id = task1.getId();
        }
        jsonResponse = gson.toJson("Добавлена задача с номером Id: " + id);
        return 201;
    }

    private static int updateTask(Task task) {
        try {
            managers.updateTask(task);
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

    private static int createSubtask(Subtask subtask) {
        managers.addSubTask(subtask);
        List<Subtask> lastSubtask = managers.getAllsubtasks();
        int id = 0;
        for (Subtask subtask1 : lastSubtask) {
            id = subtask1.getId();
        }
        jsonResponse = gson.toJson("Добавлена подзадача с номером Id: " + id);
        return 201;
    }

    private static int updateSubtask(Subtask subtask) {
        try {
            managers.updateTask(subtask);
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

    private static int createEpic(Epic epic) {
        managers.addEpic(epic);
        List<Epic> lastEpic = managers.getAllEpics();
        int id = 0;
        for (Epic epic1 : lastEpic) {
            id = epic1.getId();
        }
        jsonResponse = gson.toJson("Добавлен эпик с номером Id: " + id);
        return 201;
    }

    private static int updateEpic(Epic epic) {
        try {
            managers.updateTask(epic);
            jsonResponse = gson.toJson("Эпик обновлен");
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

    public static Task getFromBodyTask(HttpExchange httpExchange) {
        InputStream inputStream = httpExchange.getRequestBody();
        Task task = gson.fromJson(new InputStreamReader(inputStream), Task.class);
        return task;
    }

    public static Subtask getFromBodySubtask(HttpExchange httpExchange) {
        InputStream inputStream = httpExchange.getRequestBody();
        Subtask subtask = gson.fromJson(new InputStreamReader(inputStream), Subtask.class);
        return subtask;
    }

    public static Epic getFromBodyEpic(HttpExchange httpExchange) {
        InputStream inputStream = httpExchange.getRequestBody();
        Epic epic = gson.fromJson(new InputStreamReader(inputStream), Epic.class);
        return epic;
    }
}
