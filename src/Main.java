import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;


public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Что-то","Описание1", Status.NEW);
        taskManager.addTask(task);
        task = new Task("Что-то","Описание2",Status.NEW);
        taskManager.addTask(task);
        task = new Task("Что-то","Описание3",Status.NEW);
        taskManager.addTask(task);
        task = new Task("Что-то","Описание4",Status.NEW);
        taskManager.addTask(task);
        task = new Task("Что-то","Описание5",Status.NEW);
        taskManager.addTask(task);
        task = new Task("Что-то","Описание6",Status.NEW);
        taskManager.addTask(task);
        task = new Task("Что-то","Описание7",Status.NEW);
        taskManager.addTask(task);
        Epic epic = new Epic("Что-то","Описание8",Status.NEW);
        taskManager.addEpic(epic);
        Subtask subepic = new Subtask("Что-то","Описание9",Status.NEW,8);
        taskManager.addSubTask(subepic);
        epic = new Epic("Что-то","Описание10",Status.NEW);
        taskManager.addEpic(epic);
        epic = new Epic("Что-то","Описание11",Status.NEW);
        taskManager.addEpic(epic);
        task = new Task("Что-то","Описание12",Status.NEW);
        taskManager.addTask(task);

        taskManager.getTaskInId(1);
        taskManager.getTaskInId(2);
        taskManager.getTaskInId(3);
        taskManager.getTaskInId(12);
        taskManager.getTaskInId(1);
        taskManager.getTaskInId(6);
        taskManager.getTaskInId(7); //10
        taskManager.getEpicInId(8);
        taskManager.getSubTaskInId(9);
        taskManager.getEpicInId(10);
        taskManager.getEpicInId(11);
        taskManager.getTaskInId(12);
        taskManager.getEpicInId(8);
        taskManager.getTaskInId(7);
        taskManager.getTaskInId(7);
        taskManager.getTaskInId(7);
        System.out.println(taskManager.getHistory());
    }
}
