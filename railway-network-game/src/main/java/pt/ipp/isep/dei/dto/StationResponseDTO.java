package pt.ipp.isep.dei.dto;

import pt.ipp.isep.dei.domain.scenario.Station;

public class StationResponseDTO {
    private String id;
    private String name;
    private String type;
    private double cost;
    private LocationDTO location;
    private int radius;

    public StationResponseDTO(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.type = station.getType();
        this.cost = station.getCost();
        this.location = new LocationDTO(station.getLocation().getX(), station.getLocation().getY());
        this.radius = station.getRadius();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getCost() { return cost; }
    public LocationDTO getLocation() { return location; }
    public int getRadius() { return radius; }
}
