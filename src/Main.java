public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        System.out.println("Список задач:");
        System.out.println(manager.getTaskList());

        System.out.println("Список подзадач:");
        System.out.println(manager.getSubtaskList());

        System.out.println("Список эпиков:");
        System.out.println(manager.getEpicList());

        manager.deleteAllTask();
        System.out.println("Все задачи удалены!");

        System.out.println("Задача с номером " + manager.taskNumber + ":");
        manager.getTaskById();

        manager.createTask();
        System.out.println("Задача создана!");

        manager.changeTask();
        System.out.println("Задача изменена!");

        manager.deleteTaskById();
        ("Задача с номером " + manager.taskNumber + "удалена!");

        manager.getSubtaskListEpic();
        ("Список подзадач " + epic.name + " :");
        manager.getSubtaskListEpic();
    }
}
