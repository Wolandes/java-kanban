import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void testTasksAreEqualIfIdsAreEqual() {
        Task task1 = new Task("Задача 1", "Свойство 1", Status.NEW);
        Task task2 = new Task("Задача 2", "Свойство 2", Status.IN_PROGRESS);
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }

    @Test
    public void testTaskUnchangedAfterAddition() {
        Task task = new Task("Задача 1", "Свойство 1", Status.NEW);
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTaskInId(task.getId());

        assertEquals(task.getName(), retrievedTask.getName());
        assertEquals(task.getDescription(), retrievedTask.getDescription());
        assertEquals(task.getStatus(), retrievedTask.getStatus());
    }
}
