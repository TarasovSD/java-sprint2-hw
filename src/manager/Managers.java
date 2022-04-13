package manager;

public class Managers {

    /**
     * Получает реализацию класса InMemoryManager
     */
    public static TaskManager getDefault() {
        return new InMemoryManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
