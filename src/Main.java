import java.util.ArrayList;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // создаем менеджер
        Manager manager = new Manager();

        // ----------- Задачи Task ------------
        // создаем новую задачу
        Task newTask = new Task("Задача 1", "Описание задачи 1", Status.New);
        Task createdTask = manager.createTask(newTask);

        //check
        Task taskToCheck = new Task(1, "Задача 1", "Описание задачи 1", Status.New);
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
        taskToUpdate2.setStatus(Status.InProgress);
        Task updatedTask2 = manager.updateTask(taskToUpdate2);

        // удаление задачи
        Task deletedTask = manager.deleteTask(createdTask.getId());
        if (manager.getTask(createdTask.getId()) != null) {
            System.out.println("ЗАДАЧА НЕ УДАЛЕНА!");
        }

        // получение всех задач
        manager.createTask(new Task("Для теста", "Описание", Status.Done)); //добавили для теста
        ArrayList<Task> taskList = manager.getAllTasks();
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
        manager.createSubtask(new Subtask("Сабтаск к эпику 1", "Описание сабтаска", Status.New,
                createdEpic.getId()));

        //check
        ArrayList<Subtask> epicSubtasks = manager.getEpicSubtasks(createdEpic.getId());
        if (epicSubtasks.size() != 1) {
            System.out.println("ОШИБКА! Задач в эпике " + epicSubtasks.size() + " шт! Должно быть - 1 шт.");
        }

        ArrayList<Subtask> allSubtasks = manager.getAllSubtasks();
        if (allSubtasks.size() != 1) {
            System.out.println("ОШИБКА! Подзадач в списке всего  " + epicSubtasks.size() + " шт! Должно быть - 1 шт.");
        }

        if (!Objects.equals(createdEpic.getStatus(), Status.New)) {
            System.out.println("ОШИБКА! Статус эпика " + createdEpic.getStatus() + " Должен быть " + Status.New);
        }

        //Удаляем эпики
        Epic newEpic = manager.createEpic(new Epic("Эпик 2", "Новый эпик 2"));
        Epic newEpic1 = manager.createEpic(new Epic("Эпик 3", "Новый эпик 3"));
        manager.deleteEpic(5);
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getEpicSubtasks(3));
    }
}
