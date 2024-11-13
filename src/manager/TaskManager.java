package manager;

import model.Task;
import model.Epic;
import model.Subtask;

import java.util.List;


public interface TaskManager {
    //Методы Task
    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskInId(int id);

    void addTask(Task task);

    void updateTask(Task task);

    void removeTaskInId(int id);

    //Методы Epic
    List<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicInId(int id);

    void addEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicInId(int id);

    //Методы SubTask
    List<Subtask> getAllsubtasks();

    void removeAllSubTasks();

    Subtask getSubTaskInId(int id);

    void addSubTask(Subtask subtask);

    void updateSubTask(Subtask subtask);

    void removeSubTaskInId(int id);

    List<Subtask> getsubtasksInEpic(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

}
