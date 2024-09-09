import java.util.ArrayList;

public interface TaskManager {
    //Методы Task
    ArrayList<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskInId(int id);

    void addTask(Task task);

    void updateTask(Task task);

    void removeTaskInId(int id);

    //Методы Epic
    ArrayList<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicInId(int id);

    void addEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicInId(int id);

    //Методы SubTask
    ArrayList<Subtask> getAllsubtasks();

    void removeAllSubTasks();

    Subtask getSubTaskInId(int id);

    void addSubTask(Subtask subtask);

    void updateSubTask(Subtask subtask);

    void removeSubTaskInId(int id);

    ArrayList<Subtask> getsubtasksInEpic(int epicId);

    ArrayList<Task> getHistory();


}
