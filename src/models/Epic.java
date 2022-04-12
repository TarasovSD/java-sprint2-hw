package models;

import java.util.ArrayList;

/**
 * Класс Эпик
 */
public class Epic extends Task {
    private final ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.NEW);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
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
}


