import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {

    @Test
    public void testSubTaskEqualityById() {
        Subtask subTask1 = new Subtask("Подзадача 1", "Свойство 1", Status.NEW, 1);
        Subtask subTask2 = new Subtask("Подзадача 2", "Свойство 2", Status.IN_PROGRESS, 1);
        subTask1.setId(1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2);
    }

    @Test
    public void testSubTaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Эпик 1", "Св-во 1", Status.NEW);
        epic.setId(1);

        Subtask subTask = new Subtask("Подзадача 1", "Св-во 1", Status.NEW, 1);
        subTask.setId(2);

        assertNotEquals(subTask.getParentEpicId(), subTask.getId());
    }
}
