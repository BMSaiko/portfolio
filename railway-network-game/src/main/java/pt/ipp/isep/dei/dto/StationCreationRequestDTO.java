package pt.ipp.isep.dei.dto;

public class StationCreationRequestDTO {
    private int x;
    private int y;
    private String stationType;
    private String customName;

    public StationCreationRequestDTO(int x, int y, String stationType) {
        this.x = x;
        this.y = y;
        this.stationType = stationType;
    }

    // Getters and setters
    public int getX() { return x; }
    public int getY() { return y; }
    public String getStationType() { return stationType; }
    public String getCustomName() { return customName; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setStationType(String stationType) { this.stationType = stationType; }
    public void setCustomName(String customName) { this.customName = customName; }
}
