package manager;

public class Managers {

    /**
     * Получает реализацию класса InMemoryManager
     */
    public static Manager getManager() {
        return new InMemoryManager();
    }

    /**
     * Получает реализацию класса InMemoryHistoryManager
     */
    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
