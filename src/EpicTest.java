import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    public void testEpicEqualityById() {
        Epic epic1 = new Epic("Эпик 1", "Свойство 1", Status.NEW);
        Epic epic2 = new Epic("Эпик 1", "Свойство 2", Status.NEW);
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }

    @Test
    public void testEpicCannotAddItselfAsSubTask() {
        Epic epic = new Epic("Epic 1", "Description 1", Status.NEW);
        epic.setId(1);

        epic.addSubTaskId(epic.getId());

        assertFalse(epic.getSubTaskIds().contains(epic.getId()));
        assertEquals(0, epic.getSubTaskIds().size());
    }

    @Test
    public void testEpicUpdatesCorrectlyAfterSubTaskChange() {
        Epic epic = new Epic("Эпик 1", "Свойство 1", Status.NEW);
        epic.setId(1);
        Subtask subTask1 = new Subtask("Подзадача 1", "Свойство 1", Status.NEW , 1);
        subTask1.setId(2);

        epic.addSubTaskId(subTask1.getId());
        assertEquals(1, epic.getSubTaskIds().size());
        assertTrue(epic.getSubTaskIds().contains(subTask1.getId()));
    }
}
