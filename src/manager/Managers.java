package manager;

public class Managers {

    /**
     * Получает реализацию класса InMemoryManager
     */
    public static Manager getDefault() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        return new InMemoryManager(inMemoryHistoryManager);
    }
}
