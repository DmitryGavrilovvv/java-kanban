package tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.gavrilov.schedule.manager.Manager;
import ru.yandex.javacource.gavrilov.schedule.manager.TaskManager;
import ru.yandex.javacource.gavrilov.schedule.task.Epic;
import ru.yandex.javacource.gavrilov.schedule.task.Subtask;
import ru.yandex.javacource.gavrilov.schedule.task.TaskStatus;

public class EpicTest {
    @Test
    public void shouldTasksEquallyPerId() {
        TaskManager manager = Manager.getDefault();
        Epic epic = new Epic("epic1", "des1", TaskStatus.NEW);
        int id = manager.addEpic(epic);
        epic.setId(id);
        Assertions.assertEquals(epic, manager.getEpicById(id));
    }

    @Test
    public void shouldEpicCannotAddedToEpic() {
        TaskManager manager = Manager.getDefault();
        Epic epic = new Epic("epic1", "des1", TaskStatus.NEW);
        //Assertions.assertNull(manager.addSubtask(epic));
        // Как тогда делать проверки на то что нельзя положить эпик в эпик, а сабтаск в сабтаск? В ФЗ было написано что
        // такие проверки должны быть

    }
}
