package manager;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static TaskManager managers;
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .setPrettyPrinting()
            .create();
    private static String jsonResponce = "";

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler());
        /*httpServer.createContext("/subtasks", new SubTasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());*/

        httpServer.start();


    }

    static class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

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
                        String idString = pathArray[3];
                        Integer id = Integer.parseInt(idString);
                        Task task = managers.getTaskInId(id);
                        if (task != null) {
                            jsonResponce = gson.toJson(task);
                            httpExchange.sendResponseHeaders(200, 0);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonResponce.getBytes());
                                jsonResponce = "";
                            }
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                        }
                        break;
                    }
                case "POST":
                    if (lengthPath < 3) {
                        InputStream inputStream = httpExchange.getRequestBody();
                        Task task = gson.fromJson(inputStream.toString(), Task.class);
                        managers.addTask(task);
                    } else {
                        String idString = pathArray[3];
                        Integer id = Integer.parseInt(idString);
                    }
                    break;
                case "DELETE":
                    if (lengthPath > 3) {

                    } else {

                    }
                    break;
            }
        }
    }

    /*static class SubTasksHandler implements HttpHandler {

    }

    static class EpicsHandler implements HttpHandler {

    }

    static class HistoryHandler implements HttpHandler {

    }

    static class PrioritizedHandler implements HttpHandler {

    }*/
    private static void sendResponseHeaders(HttpExchange httpExchange, int rCode) throws IOException {
        if (rCode == 200){
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(jsonResponce.getBytes());
                jsonResponce = "";
            }
        } else if (rCode == 404) {
            httpExchange.sendResponseHeaders(404, 0);
        } else if (rCode == 406) {
            httpExchange.sendResponseHeaders(406, 0);
        }
    }

    private static int getAllTasks() {
        List<Task> allTasks = managers.getAllTasks();

        for (Task allTask : allTasks) {
            jsonResponce = jsonResponce + gson.toJson(allTask);
        }
        return 200;
    }

    private static void getIdTasks(HttpExchange httpExchange){

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
}
