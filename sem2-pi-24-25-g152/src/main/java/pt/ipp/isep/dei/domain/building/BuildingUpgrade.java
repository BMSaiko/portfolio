package pt.ipp.isep.dei.domain.building;

import java.util.Date;

public abstract class BuildingUpgrade {

    private String id;
    private String name;
    private BuildingType type;
    private double cost;
    private Date availableFrom;
    private String description;

    public BuildingUpgrade(String id, String name, double cost, Date availableFrom, String description) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.availableFrom = availableFrom;
        this.description = description;
    }

    public abstract String getType();
    public abstract double getMaintenanceCost();
    public abstract double getRevenue();
    public abstract boolean isCompatibleWith(String stationType);
    public abstract boolean conflictsWith(BuildingUpgrade other);

    public double getCost() {
        return cost;
    }

    public Date getAvailableFrom() {
        return availableFrom;
    }

    public boolean isAvailableAt(Date date) {
        return false;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public void setCost(double cost) { this.cost = cost; }
    public void setAvailableFrom(Date availableFrom) { this.availableFrom = availableFrom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
