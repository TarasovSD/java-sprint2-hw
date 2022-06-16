import manager.*;
import manager.HTMLController.KVServer;
import models.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {

        // создаем менеджер
        TaskManager manager = Managers.getDefault();
//
//        // ----------- Задачи models.Task ------------
//        // создаем новую задачу
//        Task newTask = new Task("Задача 1", "Описание задачи 1", Status.NEW);
//        Task createdTask = manager.createTask(newTask);
//
//
//        //check
//        Task taskToCheck = new Task(1, "Задача 1", "Описание задачи 1", Status.NEW);
//        if (!Objects.equals(createdTask, taskToCheck)) {
//            System.err.println("Ошибка! Задача не создана!");
//            return;
//        }
//
//        // обновляем описание задачи
//        // - вариант 1
//        Task taskToUpdate1 = new Task(createdTask.getId(), createdTask.getName(), "Нормальное описание задачи",
//                createdTask.getStatus());
//        Task updatedTask1 = manager.updateTask(taskToUpdate1);
//        // - вариант 2
//        // получение задачи по id
//        Task taskToUpdate2 = manager.getTask(createdTask.getId());
//        if (taskToUpdate2 == null) {
//            System.out.println("Задача не найдена! ID: " + createdTask.getId());
//            return;
//        }
//        taskToUpdate2.setDescription("Другое нормальное описание");
//        taskToUpdate2.setStatus(Status.INPROGRESS);
//        Task updatedTask2 = manager.updateTask(taskToUpdate2);
//
//        // удаление задачи
//        Task deletedTask = manager.deleteTask(createdTask.getId());
//        if (manager.getTask(createdTask.getId()) != null) {
//            System.out.println("ЗАДАЧА НЕ УДАЛЕНА!");
//        }
//
//        // получение всех задач
//        manager.createTask(new Task("Для теста", "Описание", Status.DONE)); //добавили для теста
//        List<Task> taskList = manager.getAllTasks();
//        if (taskList.size() != 1) {
//            System.out.println("Ошибка получения списка задач");
//        }
//
//        // удаление всех задач
//        manager.deleteAllTasks();
//        if (!manager.getAllTasks().isEmpty()) {
//            System.out.println("Ошибка! Все задачи не были удалены");
//        }
//
//        // -------------- SUBTASKS ------------------
//        // создаем новую подзадачу
//        Epic createdEpic = manager.createEpic(new Epic("Эпик 1", "Новый эпик 1"));
//        manager.createSubtask(new Subtask("Сабтаск к эпику 1", "Описание сабтаска", Status.NEW,
//                createdEpic.getId()));
//
//        //check
//        List<Subtask> epicSubtasks = manager.getEpicSubtasks(createdEpic.getId());
//        if (epicSubtasks.size() != 1) {
//            System.out.println("ОШИБКА! Задач в эпике " + epicSubtasks.size() + " шт! Должно быть - 1 шт.");
//        }
//
//        List<Subtask> allSubtasks = manager.getAllSubtasks();
//        if (allSubtasks.size() != 1) {
//            System.out.println("ОШИБКА! Подзадач в списке всего  " + epicSubtasks.size() + " шт! Должно быть - 1 шт.");
//        }
//
//        if (!Objects.equals(createdEpic.getStatus(), Status.NEW)) {
//            System.out.println("ОШИБКА! Статус эпика " + createdEpic.getStatus() + " Должен быть " + Status.NEW);
//        }
//
//        //Удаляем эпики
//        Epic newEpic = manager.createEpic(new Epic("Эпик 2", "Новый эпик 2"));
//        Epic newEpic1 = manager.createEpic(new Epic("Эпик 3", "Новый эпик 3"));
//        manager.deleteEpic(5);
//        System.out.println(manager.getAllEpics());
//        System.out.println(manager.getEpicSubtasks(3));
//
//        //Печатаем список последних задач
//        System.out.println("---------------------------------------------------------------");
//        System.out.println("Case 1 - Cоздайте две задачи, эпик с тремя подзадачами и эпик без подзадач");
//        TaskManager historyManagerCase1 = Managers.getDefault();
//        historyManagerCase1.createTask(new Task(1, "Задача 1", "Для истории задачи 1", Status.NEW));
//        historyManagerCase1.createTask(new Task(2, "Задача 2", "Для истории задачи 2", Status.NEW));
//        historyManagerCase1.createEpic(new Epic(3, "Эпик 1", "Эпик с 3 подзадачами"));
//        historyManagerCase1.createSubtask(new Subtask(5, "Сабтаск 1", "к эпику 2", Status.NEW,
//                3));
//        historyManagerCase1.createSubtask(new Subtask(6, "Сабтаск 2", "к эпику 2", Status.NEW,
//                3));
//        historyManagerCase1.createSubtask(new Subtask(7, "Сабтаск 3", "к эпику 2", Status.NEW,
//                3));
//        historyManagerCase1.createEpic(new Epic(4, "Эпик 4", "Эпик без задача"));
//
//        historyManagerCase1.getTask(1);
//        historyManagerCase1.getTask(2);
//        historyManagerCase1.getEpic(3);
//        historyManagerCase1.getEpic(4);
//        historyManagerCase1.getSubtask(5);
//        historyManagerCase1.getSubtask(6);
//        historyManagerCase1.getSubtask(7);
//        List<Task> historyCase1 = historyManagerCase1.getHistory();
//        System.out.println("Case 1 - Печатаем список:");
//        System.out.println(historyCase1);
//        System.out.println("Case 1 - Размер списка: " + historyCase1.size());
//        if (historyCase1.size() != 7) {
//            System.out.println("ОШИБКА!!! Case 1 - Ожидалась длина истории: " + 7 + ", но было " + historyCase1.size());
//        }
//        if (historyCase1.get(0).getId() != 1) {
//            System.out.println("ОШИБКА!!! Case 1 - Первый элемент должен быть : " + 1 + ", но было "
//                + historyCase1.get(0).getId());
//        }
//        if (historyCase1.get(historyCase1.size() - 1).getId() != 7) {
//            System.out.println("ОШИБКА!!! Case 1 - Последний элемент должен быть : " + 7 + ", но было "
//                + historyCase1.get(historyCase1.size() - 1).getId());
//        }
//        System.out.println();
//
//        System.out.println("---------------------------------------------------------------");
//        System.out.println("Case 2 - запросите созданные задачи несколько раз в разном порядке");
//        historyManagerCase1.getTask(1);
//        List<Task> historyCase2 = historyManagerCase1.getHistory();
//        if (historyCase2.size() != 7) {
//            System.out.println("ОШИБКА!!! Case 2 - Ожидалась длина истории: " + 7 + ", но было " + historyCase2.size());
//        }
//        if (historyCase2.get(0).getId() != 2) {
//            System.out.println("ОШИБКА!!! Case 2 - Первый элемент должен быть : " + 2 + ", но было "
//                + historyCase2.get(0).getId());
//        }
//        if (historyCase2.get(historyCase2.size() - 1).getId() != 1) {
//            System.out.println("ОШИБКА!!! Case 2 - Последний элемент должен быть : " + 1 + ", но было "
//                + historyCase2.get(historyCase2.size() - 1).getId());
//        }
//        historyManagerCase1.getEpic(4);
//        List<Task> historyCase3 = historyManagerCase1.getHistory();
//        if (historyCase3.size() != 7) {
//            System.out.println("ОШИБКА!!! Case 2 - Ожидалась длина истории: " + 7 + ", но было " + historyCase3.size());
//        }
//        if (historyCase3.get(historyCase3.size() - 1).getId() != 4) {
//            System.out.println("ОШИБКА!!! Case 2 - Последний элемент должен быть : " + 4 + ", но было "
//                + historyCase3.get(historyCase3.size() - 1).getId());
//        }
//
//        System.out.println("---------------------------------------------------------------");
//        System.out.println("Case 3 - удалите задачу, которая есть в истории, и проверьте, что при печати она не будетвыводиться");
//        System.out.println(" ---Удалим самую первую задачу в списке истории (задача с id 2)");
//        historyManagerCase1.deleteTask(2);
//        List<Task> historyCase4 = historyManagerCase1.getHistory();
//        if (historyCase4.size() != 6) {
//            System.out.println("ОШИБКА!!! Case 3 - Ожидалась длина истории: " + 6 + ", но было " + historyCase4.size());
//        }
//        if (historyCase4.get(0).getId() != 3) {
//            System.out.println("ОШИБКА!!! Case 3 - Первый элемент должен быть : " + 3 + ", но было "
//                + historyCase4.get(0).getId());
//        }
//        System.out.println(" ---Удалим самую последнюю задачу в списке истории (эпик без задач с id 4)");
//        historyManagerCase1.deleteEpic(4);
//        List<Task> historyCase5 = historyManagerCase1.getHistory();
//        if (historyCase5.size() != 5) {
//            System.out.println("ОШИБКА!!! Case 3 - Ожидалась длина истории: " + 5 + ", но было " + historyCase5.size());
//        }
//        if (historyCase5.get(historyCase5.size() - 1).getId() != 1) {
//            System.out.println("ОШИБКА!!! Case 3 - Последний элемент должен быть : " + 1 + ", но было "
//                + historyCase5.get(historyCase5.size() - 1).getId());
//        }
//
//        System.out.println(" ---Удалим эпик с 3 подзадачами (эпик с id 3)");
//        historyManagerCase1.deleteEpic(3);
//        List<Task> historyCase6 = historyManagerCase1.getHistory();
//        if (historyCase6.size() != 1) {
//            System.out.println("ОШИБКА!!! Case 3 - Ожидалась длина истории: " + 1 + ", но было " + historyCase6.size());
//        }
//        if (historyCase6.get(0).getId() != 1) {
//            System.out.println("ОШИБКА!!! Case 3 - Первый элемент должен быть : " + 1 + ", но было "
//                + historyCase6.get(0).getId());
//        }
//        if (historyCase6.get(historyCase6.size() - 1).getId() != 1) {
//            System.out.println("ОШИБКА!!! Case 3 - Последний элемент должен быть : " + 1 + ", но было "
//                + historyCase6.get(historyCase6.size() - 1).getId());
        }
//        System.out.println(historyManagerCase1.getHistory());
}
