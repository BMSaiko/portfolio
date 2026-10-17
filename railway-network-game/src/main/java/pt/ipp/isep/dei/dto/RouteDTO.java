package pt.ipp.isep.dei.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * Data Transfer Object for Route information.
 */
public class RouteDTO {
    private String name;
    private List<String> stations;

    /**
     * Constructor for RouteDTO.
     * 
     * @param name the route name
     */
    public RouteDTO(String name) {
        this.name = name;
        this.stations = new ArrayList<>();
    }

    /**
     * Gets the route name.
     * 
     * @return the route name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of stations in the route.
     * 
     * @return the list of stations
     */
    public List<String> getStations() {
        return new ArrayList<>(stations);
    }

    /**
     * Adds a station to the route.
     * 
     * @param station the station to add
     */
    public void addStation(String station) {
        stations.add(station);
    }

    /**
     * Sets the list of stations in the route.
     * 
     * @param stations the list of stations
     */
    public void setStations(List<String> stations) {
        this.stations = new ArrayList<>(stations);
    }

    /**
     * Returns a string representation of the route.
     * 
     * @return a string representation of the route
     */
    @Override
    public String toString() {
        if (stations.isEmpty()) {
            return name + " (no stations)";
        }
        
        return name + " (" + String.join(" → ", stations) + ")";
    }
}
