package models;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    TaskManager taskManager = Managers.getDefault();
    Task newTask;
    Task newTask1;
    Subtask subtask;
    Subtask subtask1;
    Subtask subtask2;

    Subtask subtask3;

    Subtask subtask4;
    Subtask subtask5;
    Subtask subtask6;
    Epic epic;
    Epic epic1;

    EpicTest() throws IOException, InterruptedException {
    }

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
        subtask1 = new Subtask(5, TaskTypes.SUBTASK, "Сабтаск 5", "Описание сабтаска 5", Status.NEW,
                3, LocalDateTime.of(2022, 5, 31, 9, 0), 20);
        taskManager.createSubtask(subtask1);
        subtask2 = new Subtask(6, TaskTypes.SUBTASK, "Сабтаск 6", "Описание сабтаска 6", Status.NEW,
                3, LocalDateTime.of(2022, 5, 31, 10, 0), 20);
        taskManager.createSubtask(subtask2);
        taskManager.getTask(1);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);
    }

    @Test
    void epicCalculateStartEndDuration() {
        Subtask subtaskToUpdate = new Subtask(6, TaskTypes.SUBTASK, "Сабтаск 6",
                "Новое описание сабтаска 6", Status.DONE, 3, LocalDateTime.of(2022, 5,
                31, 11, 0), 20);


        assertEquals(3, taskManager.getEpicSubtasks(3).size(),
                "Размер списка подзадач эпика должен быть: 3");
        assertEquals(epic.getStart(), subtask.getStart(), "Начало epic должно совпадать с началом subtask");
        assertEquals(epic.getEnd(), subtask2.getEnd(), "Конец epic должен совпадать с нконцом subtask2");

        taskManager.updateSubtask(subtaskToUpdate);

        assertEquals(subtaskToUpdate, epic.getSubtasks().get(2),
                "Третья подзадача эпика должна совпадать с subtaskToUpdate");
        assertEquals(Status.INPROGRESS, epic.getStatus(), "Статус эпика должен быть: INPROGRESS");
        assertEquals(epic.getEnd(), epic.getSubtasks().get(2).getEnd(),
                "Конец третьей подзадачи эпика должен совпадать с концом subtask2");
        assertEquals(subtask2.getEnd().plusHours(1), epic.getSubtasks().get(2).getEnd(),
                "Конец третьей подзадачи эпика должен совпадать с концом subtask2 до апдейта плюс час");

        subtask3 = new Subtask(7, TaskTypes.SUBTASK, "Сабтаск 6",
                "Новое описание сабтаска 7", Status.NEW, 3, LocalDateTime.of(2022, 5,
                31, 12, 0), 20);
        taskManager.createSubtask(subtask3);

        assertEquals(epic.getEnd(), epic.getSubtasks().get(3).getEnd()
                , "Конец четвертой подзадачи эпика должен совпадать с концом эпика");

        epic1 = new Epic(8, TaskTypes.EPIC, "Эпик 8", "Описание эпика 8");
        taskManager.createEpic(epic1);

        assertNull(epic1.getStart(), "Start эпика 8 должн быть: null");
        assertNull(epic1.getEnd(), "End эпика 8 должн быть: null");

        subtask4 = new Subtask(9, TaskTypes.SUBTASK, "Сабтаск 9",
                "Описание сабтаска 9", Status.NEW, 8, LocalDateTime.of(2022, 5,
                31, 13, 0), 20);
        taskManager.createSubtask(subtask4);

        assertEquals(subtask4.getStart(), taskManager.getEpic(8).getStart(),
                "Поля Start эпика 8 и сабтаска 9 должны быть равны");
        assertEquals(subtask4.getEnd(), taskManager.getEpic(8).getEnd(),
                "Поля End эпика 8 и сабтаска 9 должны быть равны");
        assertEquals(subtask4.getDuration(), taskManager.getEpic(8).getDuration(),
                "Поля Duration эпика 8 и сабтаска 9 должны быть равны");

        subtask5 = new Subtask(10, TaskTypes.SUBTASK, "Сабтаск 10",
                "Описание сабтаска 10", Status.NEW, 8, LocalDateTime.of(2022, 5,
                31, 14, 0), 20);
        taskManager.createSubtask(subtask5);

        assertEquals(subtask4.getStart(), taskManager.getEpic(8).getStart(),
                "Поля Start эпика 8 и сабтаска 9 должны быть равны");
        assertEquals(subtask5.getEnd(), taskManager.getEpic(8).getEnd(),
                "Поля End эпика 8 и сабтаска 10 должны быть равны");
        assertEquals(40, taskManager.getEpic(8).getDuration(),
                "Поле Duration эпика 8 должно быть равно: 40");

        subtask6 = new Subtask(11, TaskTypes.SUBTASK, "Сабтаск 11",
                "Описание сабтаска 11", Status.NEW, 8, LocalDateTime.of(2022, 5,
                31, 15, 0), 20);
        taskManager.createSubtask(subtask6);

        assertEquals(subtask4.getStart(), taskManager.getEpic(8).getStart(),
                "Поля Start эпика 8 и сабтаска 9 должны быть равны");
        assertEquals(subtask6.getEnd(), taskManager.getEpic(8).getEnd(),
                "Поля End эпика 8 и сабтаска 11 должны быть равны");
        assertEquals(60, taskManager.getEpic(8).getDuration(),
                "Поле Duration эпика 8 должно быть равно: 60");

        Subtask subtaskToUpdate1 = new Subtask(11, TaskTypes.SUBTASK, "Сабтаск 11",
                "Новое писание сабтаска 11", Status.DONE, 8, LocalDateTime.of(2022, 5,
                31, 16, 0), 30);
        taskManager.updateSubtask(subtaskToUpdate1);

        assertEquals(taskManager.getEpic(8).getSubtasks().get(2).getEnd(), taskManager.getEpic(8).getEnd(),
                "Поля End эпика 8 и сабтаска 11 должны быть равны");
        assertEquals(70, taskManager.getEpic(8).getDuration(),
                "Поле Duration эпика 8 должно быть равно: 70");

        taskManager.deleteSubtask(11);

        assertEquals(taskManager.getEpic(8).getSubtasks().get(1).getEnd(), taskManager.getEpic(8).getEnd(),
                "Поля End эпика 8 и сабтаска 10 должны быть равны");
        assertEquals(40, taskManager.getEpic(8).getDuration(),
                "Поле Duration эпика 8 должно быть равно: 40");

        taskManager.deleteSubtask(9);

        assertEquals(taskManager.getEpic(8).getSubtasks().get(0).getEnd(), taskManager.getEpic(8).getEnd(),
                "Поля End эпика 8 и сабтаска 10 должны быть равны");
        assertEquals(taskManager.getEpic(8).getSubtasks().get(0).getStart(), taskManager.getEpic(8).getStart(),
                "Поля End эпика 8 и сабтаска 10 должны быть равны");
        assertEquals(20, taskManager.getEpic(8).getDuration(),
                "Поле Duration эпика 8 должно быть равно: 20");

        taskManager.deleteSubtask(10);

        assertEquals(0, taskManager.getEpic(8).getSubtasks().size(),
                "Размер списка подзадач эпика должен быть: 0");
        assertNull(epic1.getStart(), "Start эпика 8 должн быть: null");
        assertNull(epic1.getEnd(), "End эпика 8 должн быть: null");
        assertEquals(0, taskManager.getEpic(8).getDuration(),
                "Поле Duration эпика 8 должно быть равно: 0");
    }
}