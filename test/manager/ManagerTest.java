package manager;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class ManagerTest<T extends TaskManager> {
    protected  T taskManager;
    Task newTask;
    Subtask subtask;
    Epic epic;

    void init(){
        newTask = new Task(1, TaskTypes.TASK, "Задача 1", "Описание задачи 1", Status.NEW,
                LocalDateTime.now(), 20);
        taskManager.createTask(newTask);
        epic = new Epic(2, TaskTypes.EPIC, "Эпик 2", "Описание эпика 2", LocalDateTime.now(), 0);
        taskManager.createEpic(epic);
        subtask = new Subtask(3, TaskTypes.SUBTASK, "Сабтаск 3", "Описание сабтаска 3", Status.NEW,
                2, LocalDateTime.now(), 20);
        taskManager.createSubtask(subtask);
        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
    }

    @Test
    void getHistory() {
        final List<Task> history = taskManager.getHistory();

        assertNotNull(history.get(0));
        assertNotNull(history.get(2));
        assertEquals(newTask, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(subtask, history.get(2));
        assertEquals(3, history.size(), "Размер: 3 задачи");
    }

    @Test
    void createTask() {
        assertEquals(1, newTask.getId());
        assertEquals("Задача 1", newTask.getName());
        assertEquals("Описание задачи 1", newTask.getDescription());
        assertEquals(Status.NEW, newTask.getStatus());
        assertEquals(TaskTypes.TASK, newTask.getTaskTypes());
        assertEquals(20, newTask.getDuration());
    }

    @Test
    void updateTask() {
        Task taskToUpdate = new Task(1, TaskTypes.TASK, "Задача 1", "Новое описание задачи 1", Status.DONE,
                 LocalDateTime.now(), 20);
        taskManager.updateTask(taskToUpdate);
        assertEquals(Status.DONE, taskManager.getTask(1).getStatus());
        assertEquals("Новое описание задачи 1", taskManager.getTask(1).getDescription());
    }

    @Test
    void getTask() {
        Task taskToCheсk = taskManager.getTask(1);
        assertNotNull(taskToCheсk);
        assertEquals(newTask, taskToCheсk);
    }

    @Test
    void deleteTask() {
        taskManager.deleteTask(1);

        assertEquals(null, taskManager.getTask(1));
        assertEquals(0, taskManager.getAllTasks().size(), "Размер списка задач: 0");
    }

    @Test
    void getAllTasks() {
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "Одна задача в списке");
        assertEquals(newTask, tasks.get(0));
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        final List<Task> tasks = taskManager.getAllTasks();

        assertEquals(null, taskManager.getTask(0));
        assertEquals(0, tasks.size(), "Размер списка задач: 0");
    }

    @Test
    void createSubtask() {
        final List<Subtask> subtasks = epic.getSubtasks();

        assertEquals(3, subtask.getId());
        assertEquals("Сабтаск 3", subtask.getName());
        assertEquals("Описание сабтаска 3", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(TaskTypes.SUBTASK, subtask.getTaskTypes());
        assertEquals(20, subtask.getDuration());
        assertEquals(2, subtask.getEpicID());
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    void updateSubtask() {
        Subtask subtaskToUpdate = new Subtask(3, TaskTypes.SUBTASK, "Сабтаск 3", "Новое писание сабтаска 3",
                Status.DONE,2, LocalDateTime.now(), 20);
        taskManager.updateSubtask(subtaskToUpdate);
        assertEquals(Status.DONE, taskManager.getSubtask(3).getStatus());
        assertEquals("Новое писание сабтаска 3", taskManager.getSubtask(3).getDescription());
    }

    @Test
    void getSubtask() {
        Subtask subtaskToCheсk = taskManager.getSubtask(3);

        assertNotNull(subtaskToCheсk);
        assertEquals(subtask, subtaskToCheсk);
    }

    @Test
    void deleteSubtask() {
        taskManager.deleteSubtask(3);

        assertEquals(null, taskManager.getSubtask(3));
        assertEquals(0, taskManager.getAllSubtasks().size(), "Размер списка задач: 0");
        assertEquals(0, epic.getSubtasks().size(), "Размер списка задач эпика: 0");
    }

    @Test
    void getAllSubtasks() {
        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size(), "Одна задача в списке");
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    void deleteAllSubtasks() {
        taskManager.deleteAllSubtasks();
        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertEquals(null, taskManager.getTask(0));
        assertEquals(0, subtasks.size(), "Размер списка подзадач: 0");
    }

    @Test
    void createEpic() {
        Subtask subtask1 = new Subtask(4, TaskTypes.SUBTASK, "Сабтаск 4", "Описание сабтаска 4",
                Status.DONE,2, LocalDateTime.now(), 20);
        taskManager.createSubtask(subtask1);
        LocalDateTime startSubtask = subtask.getStart();
        LocalDateTime endSubtask1 = subtask1.getEnd();

        assertEquals(2, epic.getId());
        assertEquals("Эпик 2", epic.getName());
        assertEquals("Описание эпика 2", epic.getDescription());
        assertEquals(Status.INPROGRESS, epic.getStatus());
        assertEquals(40, epic.getDuration());
        assertEquals(startSubtask, epic.getStart());
        assertEquals(endSubtask1, epic.getEnd());
    }

    @Test
    void updateEpic() {
        Epic epicToUpdate = epic = new Epic(2, TaskTypes.EPIC, "Эпик 2", "Новое описание эпика 2",
                LocalDateTime.now(), 0);
        taskManager.updateEpic(epicToUpdate);
        assertEquals("Новое описание эпика 2", taskManager.getEpic(2).getDescription());
    }

    @Test
    void getEpic() {
        Epic epicToCheck = taskManager.getEpic(2);

        assertEquals(epic, epicToCheck);
    }

    @Test
    void getEpicSubtasks() {
        final List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epic.getId());

        assertEquals(subtask, epicSubtasks.get(0));
        assertEquals(1, epicSubtasks.size());
        assertEquals(2, epicSubtasks.get(0).getEpicID());
    }

    @Test
    void deleteEpic() {
        taskManager.deleteEpic(2);

        assertEquals(null, taskManager.getEpic(2));
        assertEquals(0, taskManager.getAllEpics().size(), "размер списка всех эпиков: 0");
        assertEquals(null, taskManager.getSubtask(3));
        assertEquals(0, taskManager.getAllSubtasks().size(), "размер списка всех сабтасков: 0");
    }

    @Test
    void getAllEpics() {
        final List<Epic> allEpics = taskManager.getAllEpics();

        assertNotNull(allEpics);
        assertEquals(1, taskManager.getAllEpics().size(),"размер списка всех эпиков: 1");
        assertEquals(epic, allEpics.get(0));
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic(4, TaskTypes.EPIC, "Эпик 4", "Описание эпика 4",
                LocalDateTime.now(), 0);
        taskManager.createEpic(epic1);
        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "размер списка всех эпиков: 0");
        assertNull(taskManager.getEpic(2));
        assertNull(taskManager.getEpic(4));
        assertNull(taskManager.getSubtask(3));
        assertEquals(0, taskManager.getAllSubtasks().size(), "размер списка всех сабтасков: 0");
    }
}