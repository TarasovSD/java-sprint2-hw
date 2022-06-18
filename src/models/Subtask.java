package models;

import java.time.LocalDateTime;

/**
 * Класс Подзадача
 */
public class Subtask extends Task {
    private Integer epicID;

    public Subtask(Integer id, String name, String description, Status status, Integer epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, Integer epicID) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public Subtask(Integer id, TaskTypes type, String name, String description, Status status, Integer epicID) {
        super(id, type, name, description, status);
        this.epicID = epicID;
    }

    public Subtask(Integer id, TaskTypes type, String name, String description, Status status, Integer epicID, LocalDateTime start, int duration) {
        super(id, type, name, description, status, start, duration);
        this.epicID = epicID;
        this.end = start.plusMinutes(duration);
    }

    public Integer getEpicID() {
        return epicID;
    }

    public void setEpicID(Integer epicID) {
        this.epicID = epicID;
    }
}

