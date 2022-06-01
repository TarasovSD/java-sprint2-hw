package manager;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    protected TaskManager taskManager = Managers.getDefault();
    Task newTask;
    Task newTask1;
    Subtask subtask;
    Epic epic;

    @BeforeEach
    void init() {
        newTask = new Task(1, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 5, 0), 20);
        taskManager.createTask(newTask);
        newTask1 = new Task(2, TaskTypes.TASK, "Задача 2", "Описание задачи 2", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 6, 0), 20);
        taskManager.createTask(newTask1);
        epic = new Epic(3, TaskTypes.EPIC, "Эпик 3", "Описание эпика 3",
                LocalDateTime.of(2022, 5, 31, 7, 0), 0);
        taskManager.createEpic(epic);
        subtask = new Subtask(4, TaskTypes.SUBTASK, "Сабтаск 4", "Описание сабтаска 4", Status.NEW,
                3, LocalDateTime.of(2022, 5, 31, 8, 0), 20);
        taskManager.createSubtask(subtask);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);
    }

    @Test
    void add() {
        List<Task> history = taskManager.getHistory();
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);

        assertEquals(3, history.size(), "Размер списка задач должен быть равен: 3");
    }

    @Test
    void remove() {
        taskManager.deleteSubtask(4);
        List<Task> history1 = taskManager.getHistory();

        assertEquals(2, history1.size(), "Размер списка задач должен быть равен: 2");

        subtask = new Subtask(4, TaskTypes.SUBTASK, "Сабтаск 3", "Описание сабтаска 3", Status.NEW,
                3, LocalDateTime.of(2022, 5, 31, 8, 0), 20);
        taskManager.createSubtask(subtask);
        taskManager.getSubtask(4);
        taskManager.getTask(2);
        taskManager.deleteTask(2);
        List<Task> history2 = taskManager.getHistory();

        assertEquals(3, history2.size(), "Размер списка задач должен быть равен: 3");
        assertEquals(newTask, history2.get(0), "Должна быть получена задача: newTask");
        assertEquals(subtask, history2.get(2), "Должна быть получена задача: subtask");

        taskManager.deleteTask(1);

        List<Task> history3 = taskManager.getHistory();
        assertEquals(2, history3.size(), "Размер списка задач должен быть равен: 2");
        assertEquals(epic, history3.get(0), "Должна быть получена задача: epic");
        assertEquals(subtask, history3.get(1), "Должна быть получена задача: subtask");
    }

    @Test
    void getHistory() {
        InMemoryManager taskManager1 = new InMemoryManager();
        List<Task> history = taskManager.getHistory();

        assertEquals(0, taskManager1.getHistory().size(), "Размер списка задач должен быть равен: 0");
        assertEquals(3, history.size(), "Размер списка задач должен быть равен: 3");
    }
}