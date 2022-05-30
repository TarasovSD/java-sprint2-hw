package models;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Класс Эпик
 */
public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(Integer id, TaskTypes type, String name, String description) {
        super(id, type, name, description, Status.NEW);
    }

    public Epic(Integer id, String name, String description, TaskTypes type, LocalDateTime start, int duration) {
        super(id, name, description, Status.NEW, type, start, duration);
        this.duration = duration;
    }

    public Epic(Integer id, TaskTypes type, String name, String description, LocalDateTime start, int duration) {
        super(id, name, description, Status.NEW, type, start, duration);
        this.duration = duration;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public boolean removeSubtask(Subtask subtask) {
        return subtasks.remove(subtask);
    }

    @Override
    public void setDuration(int duration) {
        this.duration += duration;
    }
}


