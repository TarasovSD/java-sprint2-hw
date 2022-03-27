public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();

        Task newTask = new Task("Тест", "Описание", Status.New );
        final Task task = manager.createTask(newTask);
        final Task task1 = manager.getTaskById(task.getId());
        if (!task.equals(task1)) {
            System.out.println("Задача не найдена по ID " + task.getId());
        }
        System.out.println(manager.getTaskById(task.getId()));
        manager.deleteTaskById(task.getId());
    }
}
