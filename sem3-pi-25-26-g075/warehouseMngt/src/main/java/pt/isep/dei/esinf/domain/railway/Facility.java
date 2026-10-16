package pt.isep.dei.esinf.domain.railway;

public class Facility {
    private int id;
    private String name;
    private boolean isIntermodal;

    public Facility(int id, String name, boolean isIntermodal) {
        this.id = id;
        this.name = name;
        this.isIntermodal = isIntermodal;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public boolean isIntermodal() { return isIntermodal; }

    @Override
    public String toString() { return name; }
}
