package manager;

import exception.TimeCrossingException;
import models.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

abstract class ManagerTest<T extends TaskManager> {
    protected T taskManager;
    Task newTask;
    Subtask subtask;
    Epic epic;

    void init() {
        newTask = new Task(1, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 6, 0), 20);
        taskManager.createTask(newTask);
        epic = new Epic(2, TaskTypes.EPIC, "Эпик 2", "Описание эпика 2",
                LocalDateTime.of(2022, 5, 31, 7, 0), 0);
        taskManager.createEpic(epic);
        subtask = new Subtask(3, TaskTypes.SUBTASK, "Сабтаск 3", "Описание сабтаска 3", Status.NEW,
                2, LocalDateTime.of(2022, 5, 31, 8, 0), 20);
        taskManager.createSubtask(subtask);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
    }

    @Test
    void getHistory() {
        final List<Task> history = taskManager.getHistory();

        assertNotNull(history.get(0), "Задача не должна быть пустой");
        assertNotNull(history.get(2), "Задача не должна быть пустой");
        assertEquals(newTask, history.get(0), "Должна быть задача: newTask");
        assertEquals(epic, history.get(1), "Должен быть эпик: epic");
        assertEquals(subtask, history.get(2), "Должна быть подзадача: subtask");
        assertEquals(3, history.size(), "Размер списка истории задач: 3 задачи");
    }

    @Test
    void createTask() {
        assertNotNull(taskManager.getTask(1), "Задача не найдена");
        assertEquals(1, newTask.getId(), "ID задачи должен быть: 1");
        assertEquals("Задача 1", newTask.getName(), "Название задачи должно быть: Задача 1");
        assertEquals("Описание задачи 1", newTask.getDescription(),
                "Описание задачи должно быть: Описание задачи 1");
        assertEquals(Status.NEW, newTask.getStatus(), "Статус задачи должен быть: NEW");
        assertEquals(TaskTypes.TASK, newTask.getTaskTypes(), "Тип задачи должен быть: TASK");
        assertEquals(20, newTask.getDuration(), "Длительность задачи должна быть: 20");
        assertEquals(newTask, taskManager.getTask(1), "Задачи не совпадают");
    }

    @Test
    void updateTask() {
        Task taskToUpdate = new Task(1, TaskTypes.TASK, "Задача 1", "Новое описание задачи 1",
                Status.DONE, LocalDateTime.now(), 20);
        taskManager.updateTask(taskToUpdate);

        assertNotNull(taskManager.getTask(1), "Задача не найдена");
        assertEquals(Status.DONE, taskManager.getTask(1).getStatus(), "Статус задачи должен быть: DONE");
        assertEquals("Новое описание задачи 1", taskManager.getTask(1).getDescription(),
                "Описание задачи должно быть: Новое описание задачи 1");
    }

    @Test
    void getTask() {
        Task taskToCheсk = taskManager.getTask(1);

        assertNotNull(taskToCheсk, "Задача не найдена");
        assertEquals(newTask, taskToCheсk, "Задачи не совпадают");
        assertEquals(1, taskManager.getAllTasks().size(), "Размер списка задач должен быть: 1");
    }

    @Test
    void deleteTask() {
        taskManager.deleteTask(1);

        assertNull(taskManager.getTask(1), "Задача не удалена");
        assertEquals(0, taskManager.getAllTasks().size(), "Размер списка задач должен быть: 0");
    }

    @Test
    void getAllTasks() {
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Список задач не найден");
        assertEquals(1, tasks.size(), "Размер списка задач должен быть: 1");
        assertEquals(newTask, tasks.get(0), "Задачи не совпадают");
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        final List<Task> tasks = taskManager.getAllTasks();

        assertNull(taskManager.getTask(0), "Задачи не удалены");
        assertEquals(0, tasks.size(), "Размер списка задач должен быть: 0");
    }

    @Test
    void createSubtask() {
        final List<Subtask> subtasks = epic.getSubtasks();

        assertNotNull(subtask, "Подзадача не найдена");
        assertEquals(3, subtask.getId(), "ID подзадачи должен быть: 3");
        assertEquals("Сабтаск 3", subtask.getName(), "Название подзадачи должно быть: Сабтаск 3");
        assertEquals("Описание сабтаска 3", subtask.getDescription(),
                "Описание подзадачи должно быть: Описание сабтаска 3");
        assertEquals(Status.NEW, subtask.getStatus(), "Статус подзадачи должен быть: NEW");
        assertEquals(TaskTypes.SUBTASK, subtask.getTaskTypes(), "Подзадачи не совпадают");
        assertEquals(20, subtask.getDuration(), "Длительность подзадачи должна быть: 20");
        assertEquals(2, subtask.getEpicID(), "Должен быть эпик: 2");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void updateSubtask() {
        Subtask subtaskToUpdate = new Subtask(3, TaskTypes.SUBTASK,
                "Сабтаск 3", "Новое писание сабтаска 3",
                Status.DONE, 2, LocalDateTime.now(), 20);
        taskManager.updateSubtask(subtaskToUpdate);

        assertNotNull(taskManager.getSubtask(3), "Подзадача не найдена");
        assertEquals(Status.DONE, taskManager.getSubtask(3).getStatus(),
                "Статус подзадачи должен быть: DONE");
        assertEquals("Новое писание сабтаска 3", taskManager.getSubtask(3).getDescription(),
                "Описание подзадачи должно быть: Новое писание сабтаска 3");
    }

    @Test
    void getSubtask() {
        Subtask subtaskToCheсk = taskManager.getSubtask(3);

        assertNotNull(subtaskToCheсk, "Подзадача не найдена");
        assertEquals(subtask, subtaskToCheсk, "Подзадачи не совпадают");
        assertEquals(1, taskManager.getAllTasks().size(), "Размер списка подзадач должен быть: 1");
    }

    @Test
    void deleteSubtask() {
        taskManager.deleteSubtask(3);

        assertNull(taskManager.getSubtask(3), "Подзадача не удалена");
        assertEquals(0, taskManager.getAllSubtasks().size(), "Размер списка подзадач должен быть: 0");
        assertEquals(0, epic.getSubtasks().size(), "Размер списка подзадач эпика должен быть: 0");
    }

    @Test
    void getAllSubtasks() {
        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Список подзадач не найден");
        assertEquals(1, subtasks.size(), "Размер списка подзадач должен быть: 1");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
    }

    @Test
    void deleteAllSubtasks() {
        taskManager.deleteAllSubtasks();
        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNull(taskManager.getTask(0), "Подзадача не удалена");
        assertEquals(0, subtasks.size(), "Размер списка подзадач должен быть: 0");
    }

    @Test
    void createEpic() {
        Subtask subtask1 = new Subtask(4, TaskTypes.SUBTASK, "Сабтаск 4", "Описание сабтаска 4",
                Status.DONE, 2, LocalDateTime.now(), 20);
        taskManager.createSubtask(subtask1);
        LocalDateTime startSubtask = subtask.getStart();
        LocalDateTime endSubtask1 = subtask1.getEnd();
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(2, epic.getId(), "ID эпика должен быть: 2");
        assertEquals("Эпик 2", epic.getName(), "Название эпика должно быть: Эпик 2");
        assertEquals("Описание эпика 2", epic.getDescription(),
                "Описание эпика должно быть: Описание эпика 2");
        assertEquals(Status.INPROGRESS, epic.getStatus(), "Статус эпика должен быть: INPROGRESS");
        assertEquals(40, epic.getDuration(), "Длительность эпика должна быть: 40");
        assertEquals(startSubtask, epic.getStart(), "Начало эпика должно совпадать с началом первой подзадчи");
        assertEquals(endSubtask1, epic.getEnd(), "Конец эпика должен совпадать с концом последней подзадчи");
        assertEquals(2, taskManager.getEpicSubtasks(2).size(), "Размер списка подзадач должен быть: 2");
        assertEquals(epic, epics.get(0), "Эпики не совпадают");
    }

    @Test
    void updateEpic() {
        Epic epicToUpdate = new Epic(2, TaskTypes.EPIC, "Эпик 2", "Новое описание эпика 2",
                LocalDateTime.now(), 0);
        taskManager.updateEpic(epicToUpdate);

        assertNotNull(taskManager.getEpic(2), "Эпик не найден");
        assertEquals("Новое описание эпика 2", taskManager.getEpic(2).getDescription(),
                "Новое описание эпика должно быть: Новое описание эпика 2");
    }

    @Test
    void getEpic() {
        Epic epicToCheck = taskManager.getEpic(2);

        assertNotNull(epicToCheck, "Эпик не найден");
        assertEquals(epic, epicToCheck, "Эпики не совпадают");
        assertEquals(1, taskManager.getAllEpics().size(), "Размер списка эпиков должен быть: 1");
    }

    @Test
    void getEpicSubtasks() {
        final List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epic.getId());

        assertEquals(subtask, epicSubtasks.get(0), "Подзадачи эпика не совпадают");
        assertEquals(1, epicSubtasks.size(), "Размер списка подзадач эпика должен быть: 1");
        assertEquals(2, epicSubtasks.get(0).getEpicID(), "ID эпика в подзадаче эпика должен быть: 2");
    }

    @Test
    void deleteEpic() {
        taskManager.deleteEpic(2);

        assertNull(taskManager.getEpic(2), "Эпик не удален");
        assertEquals(0, taskManager.getAllEpics().size(), "размер списка всех эпиков должен быть: 0");
        assertNull(taskManager.getSubtask(3), "Подзадача эпика не удалена");
        assertEquals(0, taskManager.getAllSubtasks().size(), "размер списка всех сабтасков должен быть: 0");
    }

    @Test
    void getAllEpics() {
        final List<Epic> allEpics = taskManager.getAllEpics();

        assertNotNull(allEpics, "Список эпиков не должен быть пуст");
        assertEquals(1, taskManager.getAllEpics().size(), "размер списка всех эпиков должен быть: 1");
        assertEquals(epic, allEpics.get(0), "Эпики не соответствуют");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic(4, TaskTypes.EPIC, "Эпик 4", "Описание эпика 4",
                LocalDateTime.now(), 0);
        taskManager.createEpic(epic1);
        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "размер списка всех эпиков должен быть: 0");
        assertNull(taskManager.getEpic(2), "Эпик 2 не удален");
        assertNull(taskManager.getEpic(4), "Эпик 4 не удален");
        assertNull(taskManager.getSubtask(3), "Подзадача 3 не удалена");
        assertEquals(0, taskManager.getAllSubtasks().size(),
                "размер списка всех сабтасков должен быть: 0");
    }

    @Test
    void computeEpicStatus() {
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть: NEW");

        Subtask subtask1 = new Subtask(4, TaskTypes.SUBTASK, "Сабтаск 4", "Описание сабтаска 4",
                Status.DONE, 2, LocalDateTime.now(), 20);
        taskManager.createSubtask(subtask1);

        assertEquals(Status.INPROGRESS, epic.getStatus(), "Статус эпика должен быть: INPROGRESS");

        taskManager.updateSubtask(new Subtask(3, TaskTypes.SUBTASK, "Сабтаск 3",
                "Описание сабтаска 3", Status.DONE, 2, LocalDateTime.of(2022, 5, 31, 8, 0), 20));

        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть: DONE");

        Epic epic1 = new Epic(5, TaskTypes.EPIC, "Эпик 5", "Описание эпика 5",
                LocalDateTime.of(2022, 5, 31, 11, 0), 0);
        taskManager.createEpic(epic1);

        assertEquals(Status.NEW, epic1.getStatus(), "Статус эпика должен быть: NEW");
    }

    @Test
    void getPrioritizedTasks() {
        final TreeSet<Task> sortedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(taskManager.getPrioritizedTasks(), "Список не может быть null");
        assertEquals(2, sortedTasks.size(), "Размер списка должен быть: 2");
        assertEquals(newTask, taskManager.getPrioritizedTasks().first(), "Первый элемент должен быть: newTask");
        assertEquals(subtask, taskManager.getPrioritizedTasks().last(), "Последний элемент должен быть: subtask");

        Task newTask1 = new Task(4, TaskTypes.TASK, "Задача 4", "Описание задачи 4", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 6, 10), 20);
        Task newTask2 = new Task(5, TaskTypes.TASK, "Задача 5", "Описание задачи 5", Status.NEW,
                LocalDateTime.of(2022, 5, 31, 5, 50), 20);

        final TimeCrossingException exception = assertThrows(
                TimeCrossingException.class,
                () -> taskManager.createTask(newTask1)
        );
        final TimeCrossingException exception1 = assertThrows(
                TimeCrossingException.class,
                () -> taskManager.createTask(newTask2)
        );

        assertEquals("Задача пересекает ранее созданные задачи", exception.getMessage());
        assertEquals("Задача пересекает ранее созданные задачи", exception1.getMessage());

        Subtask subtask1 = new Subtask(6, TaskTypes.SUBTASK, "Сабтаск 6", "Описание сабтаска 6", Status.NEW,
                2, LocalDateTime.of(2022, 5, 31, 7, 50), 20);
        Subtask subtask2 = new Subtask(7, TaskTypes.SUBTASK, "Сабтаск 7", "Описание сабтаска 7", Status.NEW,
                2, LocalDateTime.of(2022, 5, 31, 8, 10), 20);

        final TimeCrossingException exception2 = assertThrows(
                TimeCrossingException.class,
                () -> taskManager.createSubtask(subtask1)
        );
        final TimeCrossingException exception3 = assertThrows(
                TimeCrossingException.class,
                () -> taskManager.createSubtask(subtask2)
        );

        assertEquals("Подзадача пересекает ранее созданные задачи", exception2.getMessage());
        assertEquals("Подзадача пересекает ранее созданные задачи", exception3.getMessage());

        Task newTask3 = new Task(8, TaskTypes.TASK, "Задача 8", "Описание задачи 8", Status.NEW);
        taskManager.createTask(newTask3);
        final TreeSet<Task> sortedTasks1 = taskManager.getPrioritizedTasks();

        assertEquals(newTask3, sortedTasks1.last(), "Последний элемент должен быть: newTask3");
    }
}