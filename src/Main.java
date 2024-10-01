import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import model.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class Main {

    public static void main(String[] args) {
        TaskManager managers = Managers.getDefault();
        Task task1 = new Task("Name1","Description1",Status.NEW);
        Task task2 = new Task("Name2","Description2",Status.NEW);
        Epic epicWithSubtasks = new Epic("Name1","Description1",Status.NEW);
        int idEpic = 3;
        Epic epicWithOutSubtasks = new Epic("Name1","Description1",Status.NEW);
        Subtask subtask1 = new Subtask("Name1","Description1",Status.NEW,idEpic);
        Subtask subtask2 = new Subtask("Name2","Description2",Status.NEW,idEpic);
        Subtask subtask3 = new Subtask("Name2","Description3",Status.NEW,idEpic);
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

        for(Task list1 :list){
            System.out.println(list1);
        }
        System.out.println("Удаление");
        managers.removeEpicInId(3);
        List<Task> list2 = managers.getHistory();
        for(Task list1 :list2){
            System.out.println(list1);
        }
       /* TaskManager taskManager = Managers.getDefault();
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
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("name","description",Status.NEW);
        task.setId(1);
        Task task1 = new Task("name1","description1",Status.NEW);
        task1.setId(2);
        Task task2 = new Task("name2","description2",Status.NEW);
        task2.setId(3);
        historyManager.add(task);
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        historyManager.remove(1);
        historyManager.remove(2);
        List<Task> history1 = historyManager.getHistory();
        System.out.println();
        assertEquals(history,history1,"Они не равны");*/

    }
}
