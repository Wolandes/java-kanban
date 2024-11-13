package manager;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpServerInitializer {
    private static final int PORT = 8080;
    private HttpServer httpServer;

    public HttpServerInitializer() throws IOException {
        initializeServer();
    }

    private void initializeServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new HttpTaskServer.TaskHandler());
        httpServer.createContext("/subtasks", new HttpTaskServer.SubTasksHandler());
        httpServer.createContext("/epics", new HttpTaskServer.EpicsHandler());
        httpServer.createContext("/history", new HttpTaskServer.HistoryHandler());
        httpServer.createContext("/prioritized", new HttpTaskServer.PrioritizedHandler());
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
