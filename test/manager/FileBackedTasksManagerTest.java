package manager;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;

import static models.TaskTypes.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends ManagerTest<FileBackedTasksManager> {
    @Override
    @BeforeEach
    void init() {
        taskManager = new FileBackedTasksManager(new File("task1.csv"));

        super.init();
    }

    @Test
    void saveTaskToFile() {





    }


}