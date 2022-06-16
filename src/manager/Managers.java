package manager;

import manager.HTMLController.HTTPTaskManager;

import java.io.File;
import java.io.IOException;

public class Managers {

    /**
     * Получает реализацию класса InMemoryManager
     */
    public static TaskManager getDefault() {
            return new HTTPTaskManager("http://localhost:8060/");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
