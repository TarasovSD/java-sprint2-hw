package models;

import java.util.ArrayList;

/**
 * Класс Эпик
 */
public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(Integer id, String name, String description) {
        super(id, name, description, Status.New);
    }

    public Epic(String name, String description) {
        super(name, description, Status.New);
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


