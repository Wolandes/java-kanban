package test;
import manager.HistoryManager;
import manager.Managers;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void testTasksAddedToHistoryRetainOriginalData() {
        Task task = new Task("Зд 1", "Св 1", Status.NEW);
        task.setId(0);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        Task retrievedTask = history.get(0);

        assertEquals(task.getName(), retrievedTask.getName());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }
    //Следующем тесте сразу проверяется и add и Remove (remove по id Map и по Node)
    @Test
    public void shouldRemoveData(){
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
//        Task firsTask = history.get(0);
//        int firstId = firsTask.getId();
//        Task twoTask = history1.get(0);
//        int twoId = twoTask.getId();
        assertNotEquals(history1,history, "Они равны");
    }
    //В этом тесте смотрится что записывается в конце
    @Test void shouldAddInTail(){
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
        Task result = history.get(2);
        assertEquals(task2,result, "Записался не в то место");
    }
}
