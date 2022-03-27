/**
 * Класс Подзадача
  */
public class Subtask extends Task{
    private int epicID;

    public Subtask(int id, String name, String description, String status, int epicID) {
        super(id, name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }
}

