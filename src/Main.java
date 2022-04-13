import manager.*;
import models.*;

import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // создаем менеджер
        Manager manager = Managers.getDefault();

        // ----------- Задачи models.Task ------------
        // создаем новую задачу
        Task newTask = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        Task createdTask = manager.createTask(newTask);

        //check
        Task taskToCheck = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
        if (!Objects.equals(createdTask, taskToCheck)) {
            System.err.println("Ошибка! Задача не создана!");
            return;
        }

        // обновляем описание задачи
        // - вариант 1
        Task taskToUpdate1 = new Task(createdTask.getId(), createdTask.getName(), "Нормальное описание задачи",
                createdTask.getStatus());
        Task updatedTask1 = manager.updateTask(taskToUpdate1);
        // - вариант 2
        // получение задачи по id
        Task taskToUpdate2 = manager.getTask(createdTask.getId());
        if (taskToUpdate2 == null) {
            System.out.println("Задача не найдена! ID: " + createdTask.getId());
            return;
        }
        taskToUpdate2.setDescription("Другое нормальное описание");
        taskToUpdate2.setStatus(Status.INPROGRESS);
        Task updatedTask2 = manager.updateTask(taskToUpdate2);

        // удаление задачи
        Task deletedTask = manager.deleteTask(createdTask.getId());
        if (manager.getTask(createdTask.getId()) != null) {
            System.out.println("ЗАДАЧА НЕ УДАЛЕНА!");
        }

        // получение всех задач
        manager.createTask(new Task("Для теста", "Описание", Status.DONE)); //добавили для теста
        List<Task> taskList = manager.getAllTasks();
        if (taskList.size() != 1) {
            System.out.println("Ошибка получения списка задач");
        }

        // удаление всех задач
        manager.deleteAllTasks();
        if (!manager.getAllTasks().isEmpty()) {
            System.out.println("Ошибка! Все задачи не были удалены");
        }

        // -------------- SUBTASKS ------------------
        // создаем новую подзадачу
        Epic createdEpic = manager.createEpic(new Epic("Эпик 1", "Новый эпик 1"));
        manager.createSubtask(new Subtask("Сабтаск к эпику 1", "Описание сабтаска", Status.NEW,
                createdEpic.getId()));

        //check
        List<Subtask> epicSubtasks = manager.getEpicSubtasks(createdEpic.getId());
        if (epicSubtasks.size() != 1) {
            System.out.println("ОШИБКА! Задач в эпике " + epicSubtasks.size() + " шт! Должно быть - 1 шт.");
        }

        List<Subtask> allSubtasks = manager.getAllSubtasks();
        if (allSubtasks.size() != 1) {
            System.out.println("ОШИБКА! Подзадач в списке всего  " + epicSubtasks.size() + " шт! Должно быть - 1 шт.");
        }

        if (!Objects.equals(createdEpic.getStatus(), Status.NEW)) {
            System.out.println("ОШИБКА! Статус эпика " + createdEpic.getStatus() + " Должен быть " + Status.NEW);
        }

        //Удаляем эпики
        Epic newEpic = manager.createEpic(new Epic("Эпик 2", "Новый эпик 2"));
        Epic newEpic1 = manager.createEpic(new Epic("Эпик 3", "Новый эпик 3"));
        manager.deleteEpic(5);
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getEpicSubtasks(3));

        //Печатаем список последних задач
        Manager historyManager = Managers.getDefault();
        historyManager.createTask(new Task("Задача", "Для истории", Status.NEW));
        historyManager.getTask(1);
        historyManager.createEpic(new Epic("Эпик 2", "Новый эпик 2"));
        historyManager.getEpic(2);
        System.out.println("Печатаем список:");
        System.out.println(historyManager.getHistory());

        for (int i = 0; i < 100; i++) {
            historyManager.getTask(1);
        }
        System.out.println("Размер списка: " + historyManager.getHistory().size());
    }
}
