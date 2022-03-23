import java.util.ArrayList;

/**
 * Класс Эпик
 */
public class Epic extends Task {
    private ArrayList<Subtask> subtascs;

    public Epic(String name, String description, int id, String status, ArrayList<Subtask> subtascs) {
        super(name, description, id, status);
        this.subtascs = subtascs;
    }

    public ArrayList<Subtask> getSubtascs() {
        return subtascs;
    }

    public void setSubtascs(ArrayList<Subtask> subtascs) {
        this.subtascs = subtascs;
    }
}


