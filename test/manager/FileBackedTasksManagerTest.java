package manager;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;

import static models.TaskTypes.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends ManagerTest<FileBackedTasksManager> {
    @Override
    @BeforeEach
    void init() {
        taskManager = new FileBackedTasksManager(new File("task1.csv"));
        super.init();
    }

    @Test
    void loadFromFile() {
        FileBackedTasksManager manager1 = FileBackedTasksManager.loadFromFile(new File("/Users/macbookpro/Desktop/Учеба/java-sprint2-hw/test.csv"));

        assertEquals(0, manager1.getAllTasks().size(), "список задач должен быть пуст");
        assertEquals(0, manager1.getAllEpics().size(), "список эпиков должен быть пуст");
        assertEquals(0, manager1.getAllSubtasks().size(), "список подзадач должен быть пуст");
        assertEquals(0, manager1.getHistory().size(), "список истории должен быть пуст");

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File("/Users/macbookpro/Desktop/Учеба/java-sprint2-hw/test2.csv"));

        assertEquals(1, manager2.getAllEpics().size(), "список эпиков должен быть равен: 1");


    }

    @Test
    void save() {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(new File("check.csv"));
        Task taskToCheck = new Task(1, TASK, "Задача 1", "Описание задачи 1", Status.NEW, LocalDateTime.of(2022, 5, 31, 6, 0), 20);
        fileBackedTasksManager.createTask(taskToCheck);
        fileBackedTasksManager.createTask(new Task(2, TASK, "Задача 2", "Описание задачи 2",
                Status.NEW, LocalDateTime.of(2022, 5, 31, 6, 0), 20));
        fileBackedTasksManager.createEpic(new Epic(3, EPIC, "Эпик 3", "Описание эпика 3", LocalDateTime.of(2022, 5, 31, 7, 0), 0));
        fileBackedTasksManager.createSubtask(new Subtask(4, SUBTASK, "Сабтаск 4", "Описание задачи 4",
                Status.NEW, 3, LocalDateTime.of(2022, 5, 31, 8, 0), 20));
        fileBackedTasksManager.createSubtask(new Subtask(5, SUBTASK, "Сабтаск 5", "Описание задачи 5",
                Status.NEW, 3, LocalDateTime.of(2022, 5, 31, 9, 0), 20));
        fileBackedTasksManager.getTask(1);
        fileBackedTasksManager.getEpic(3);
        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(new File("/Users/macbookpro/Desktop/Учеба/java-sprint2-hw/check.csv"));

        assertEquals(2, manager2.getAllTasks().size(), "список задач должен быть равен: 2");
        assertEquals(1, manager2.getAllEpics().size(), "список эпиков должен быть равен: 1");
        assertEquals(2, manager2.getAllSubtasks().size(), "список подзадач должен быть равен: 2");
        assertEquals(taskToCheck, manager2.getTask(1), "Должна быть: taskToCheck");
        assertEquals(taskToCheck, manager2.getTask(1), "Должна быть: taskToCheck");
        assertEquals(2, manager2.getHistory().size(), "список истории должен быть равен: 2");

    }


}