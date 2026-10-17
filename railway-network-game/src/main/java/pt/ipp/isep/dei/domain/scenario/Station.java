package pt.ipp.isep.dei.domain.scenario;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;
import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.domain.map.Map;

import java.util.List;

public abstract class Station {
    protected String id;
    protected String name;
    protected Location location;
    protected Map map;
    private List<BuildingUpgrade> buildings;

    public Station(int x, int y, String name, Map map) {
        this.location = new Location(x, y);
        this.name = name;
        this.map = map;
    }

    public abstract double getCost();
    public abstract int getRadius();
    public abstract String getType();

    public String getName() { return name; }
    public Location getLocation() { return location; }

    // Getters and setters
    public String getId() { return id; }
    public Map getMap() { return map; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLocation(Location location) { this.location = location; }
    public void setMap(Map map) { this.map = map; }
    public boolean addUpgrade(BuildingUpgrade buildingUpgrade) {
        return false;
    }
    public abstract boolean validateUpgrade(BuildingUpgrade buildingUpgrade);
    public abstract List<String> getCompatibleBuildingTypes();
    public abstract int getMaxBuildingsAllowed();
    public List<BuildingUpgrade> getBuildings() {
        return buildings;
    }

    public boolean canAffordUpgrade(BuildingUpgrade upgrade, double budget) {
        return false;
    }
}
