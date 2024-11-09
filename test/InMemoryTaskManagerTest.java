import manager.InMemoryTaskManager;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    // Тесты добавления и нахождения задач
    @Test
    public void testAddAndFindTasksById() {
        Task task = new Task("Задача 1", "Свойство 1", Status.NEW);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskInId(task.getId()));
    }

    @Test
    public void testNoConflictBetweenGeneratedAndGivenIds() {
        Task task1 = new Task("Задача 1", "Свойство 1", Status.NEW);
        task1.setId(100);
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Свойство 2", Status.IN_PROGRESS);
        taskManager.addTask(task2);

        assertNotEquals(task1.getId(), task2.getId());
    }

    // Тесты расчета статуса Epic
    @Test
    public void testEpicStatusWhenAllSubtasksAreNew() {
        Epic epic = new Epic("Эпик", "Эпик с подзадачами NEW", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Свойство подзадачи 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Свойство подзадачи 2", Status.NEW, epic.getId());
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        assertEquals(Status.NEW, taskManager.getEpicInId(epic.getId()).getStatus());
    }

    @Test
    public void testEpicStatusWhenAllSubtasksAreDone() {
        Epic epic = new Epic("Эпик", "Эпик с подзадачами DONE", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Свойство подзадачи 1", Status.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Свойство подзадачи 2", Status.DONE, epic.getId());
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        assertEquals(Status.DONE, taskManager.getEpicInId(epic.getId()).getStatus());
    }

    @Test
    public void testEpicStatusWhenSubtasksAreNewAndDone() {
        Epic epic = new Epic("Эпик", "Эпик с подзадачами NEW и DONE", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Свойство подзадачи 1", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Свойство подзадачи 2", Status.DONE, epic.getId());
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicInId(epic.getId()).getStatus());
    }

    @Test
    public void testEpicStatusWhenSubtasksAreInProgress() {
        Epic epic = new Epic("Эпик", "Эпик с подзадачами IN_PROGRESS", Status.NEW);
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Свойство подзадачи 1", Status.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Свойство подзадачи 2", Status.IN_PROGRESS, epic.getId());
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpicInId(epic.getId()).getStatus());
    }

    // Тесты истории
    @Test
    public void testEmptyHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryWithDuplicates() {
        Task task = new Task("Задача", "Задача с дубликатами в истории", Status.NEW);
        taskManager.addTask(task);
        taskManager.getTaskInId(task.getId());
        taskManager.getTaskInId(task.getId());

        assertEquals(1, taskManager.getHistory().size());
    }

    @Test
    public void testRemovingFromHistory() {
        Task task1 = new Task("Задача 1", "Первая задача", Status.NEW);
        Task task2 = new Task("Задача 2", "Вторая задача", Status.NEW);
        Task task3 = new Task("Задача 3", "Третья задача", Status.NEW);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTaskInId(task1.getId());
        taskManager.getTaskInId(task2.getId());
        taskManager.getTaskInId(task3.getId());

        taskManager.removeTaskInId(task1.getId());
        assertEquals(2, taskManager.getHistory().size());
        assertFalse(taskManager.getHistory().contains(task1));

        taskManager.removeTaskInId(task2.getId());
        assertEquals(1, taskManager.getHistory().size());
        assertFalse(taskManager.getHistory().contains(task2));

        taskManager.removeTaskInId(task3.getId());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    // Проверка пересечения задач
    @Test
    public void testTaskTimeOverlap() {
        Task task1 = new Task("Задача 1", "Первая задача", Status.NEW);
        task1.setStartTime(LocalDateTime.of(2023, 11, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Задача, пересекающаяся по времени", Status.NEW);
        task2.setStartTime(LocalDateTime.of(2023, 11, 1, 10, 30));
        task1.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> taskManager.addTask(task2));
    }
}
