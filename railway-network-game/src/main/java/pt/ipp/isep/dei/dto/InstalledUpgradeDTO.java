package pt.ipp.isep.dei.dto;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;

public class InstalledUpgradeDTO {
    private String id;
    private String name;
    private String type;
    private double maintenanceCost;
    private double currentRevenue;

    public InstalledUpgradeDTO(BuildingUpgrade upgrade) {
        // TODO: Implementation - populate from upgrade
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getMaintenanceCost() { return maintenanceCost; }
    public void setMaintenanceCost(double maintenanceCost) { this.maintenanceCost = maintenanceCost; }
    public double getCurrentRevenue() { return currentRevenue; }
    public void setCurrentRevenue(double currentRevenue) { this.currentRevenue = currentRevenue; }
}
