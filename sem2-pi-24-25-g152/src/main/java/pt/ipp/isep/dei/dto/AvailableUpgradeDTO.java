package pt.ipp.isep.dei.dto;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;

import java.util.List;

public class AvailableUpgradeDTO {
    private String id;
    private String name;
    private String type;
    private double cost;
    private String description;
    private double maintenanceCost;
    private double expectedRevenue;
    private boolean isAffordable;
    private List<String> conflictsWithExisting;

    public AvailableUpgradeDTO(BuildingUpgrade upgrade, boolean isAffordable, List<String> conflicts) {
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getMaintenanceCost() { return maintenanceCost; }
    public void setMaintenanceCost(double maintenanceCost) { this.maintenanceCost = maintenanceCost; }
    public double getExpectedRevenue() { return expectedRevenue; }
    public void setExpectedRevenue(double expectedRevenue) { this.expectedRevenue = expectedRevenue; }
    public boolean isAffordable() { return isAffordable; }
    public void setAffordable(boolean affordable) { isAffordable = affordable; }
    public List<String> getConflictsWithExisting() { return conflictsWithExisting; }
    public void setConflictsWithExisting(List<String> conflictsWithExisting) { this.conflictsWithExisting = conflictsWithExisting; }
}
