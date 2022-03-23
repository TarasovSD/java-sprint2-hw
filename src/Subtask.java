/**
 * Класс Подзадача
  */
public class Subtask extends Task{
    private int epicID;

    public Subtask(String name, String description, int id, String status, int epicID) {
        super(name, description, id, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }
}

