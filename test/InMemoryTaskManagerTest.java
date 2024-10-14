import manager.InMemoryTaskManager;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAddAndFindTasksById()  {
        Task task = new Task("Задача 1", "Свойство 1", Status.NEW);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskInId(task.getId()));
    }

    @Test
    public void testNoConflictBetweenGeneratedAndGivenIds()  {
        Task task1 = new Task("Задача 1", "Свойство 1", Status.NEW);
        task1.setId(100);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Свойство 2", Status.IN_PROGRESS);
        taskManager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }
}
