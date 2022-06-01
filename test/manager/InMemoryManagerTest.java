package manager;

import models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryManagerTest extends ManagerTest<InMemoryManager> {

    @Override
    @BeforeEach
    void init() {
        taskManager = new InMemoryManager();
        super.init();
    }
}