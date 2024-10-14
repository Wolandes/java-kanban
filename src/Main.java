import manager.ManagerSaveException;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager managers = Managers.getDefault();
        Task task1 = new Task("Name1", "Description1", Status.NEW);
        Task task2 = new Task("Name2", "Description2", Status.NEW);
        Epic epicWithSubtasks = new Epic("Name1", "Description1", Status.NEW);
        int idEpic = 3;
        Epic epicWithOutSubtasks = new Epic("Name1", "Description1", Status.NEW);
        Subtask subtask1 = new Subtask("Name1", "Description1", Status.NEW, idEpic);
        Subtask subtask2 = new Subtask("Name2", "Description2", Status.NEW, idEpic);
        Subtask subtask3 = new Subtask("Name2", "Description3", Status.NEW, idEpic);
        managers.addTask(task1);
        managers.addTask(task2);
        managers.addEpic(epicWithSubtasks);
        managers.addEpic(epicWithOutSubtasks);
        managers.addSubTask(subtask1);
        managers.addSubTask(subtask2);
        managers.addSubTask(subtask3);

        managers.getTaskInId(1);
        managers.getEpicInId(3);
        managers.getTaskInId(2);
        managers.getSubTaskInId(5);
        managers.getEpicInId(4);
        managers.getSubTaskInId(7);
        managers.getSubTaskInId(6);

        List<Task> list = managers.getHistory();

        for (Task list1 : list) {
            System.out.println(list1);
        }
        System.out.println("Удаление");
        managers.removeEpicInId(3);
        List<Task> list2 = managers.getHistory();
        for (Task list1 : list2) {
            System.out.println(list1);
        }
        /*managers.removeAllEpics();
        managers.removeTaskInId(1);
        managers.removeAllTasks();
        managers.removeTaskInId(1);
        managers.removeAllEpics();
        managers.removeSubTaskInId(4);
        managers.removeAllSubTasks();*/
    }
}
