package manager;

import manager.HTMLController.HTTPTaskManager;

import java.io.File;
import java.io.IOException;

public class Managers {

    /**
     * Получает реализацию класса InMemoryManager
     */
    public static TaskManager getDefault() throws IOException, InterruptedException {
            return new HTTPTaskManager("http://localhost:8070/");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
