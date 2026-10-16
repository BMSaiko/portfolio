package pt.ipp.isep.dei.domain.map;

public class Building {
    private int id;
    private String type;
    private int level;

    public Building(int id, String type, int level) {
        this.id = id;
        this.type = type;
        this.level = level;
    }

    public String getDescription() {
        return "Building: " + type + " | Level: " + level;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}