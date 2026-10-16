package pt.ipp.isep.dei.dto;

public class StationUpgradeRequestDTO {
    private String stationId;
    private String upgradeId;
    private double playerBudget;

    public StationUpgradeRequestDTO(String stationId, String upgradeId, double playerBudget) {
        this.stationId = stationId;
        this.upgradeId = upgradeId;
        this.playerBudget = playerBudget;
    }

    // Getters and setters
    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }
    public String getUpgradeId() { return upgradeId; }
    public void setUpgradeId(String upgradeId) { this.upgradeId = upgradeId; }
    public double getPlayerBudget() { return playerBudget; }
    public void setPlayerBudget(double playerBudget) { this.playerBudget = playerBudget; }
}
