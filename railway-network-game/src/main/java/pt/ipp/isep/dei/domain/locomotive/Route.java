package pt.ipp.isep.dei.domain.locomotive;

import pt.ipp.isep.dei.domain.map.Station;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a route that a train can follow.
 */
public class Route {
    private String name;
    private List<Station> stations;

    /**
     * Constructor for a route.
     * 
     * @param name the route name
     */
    public Route(String name) {
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
    public List<Station> getStations() {
        return new ArrayList<>(stations);
    }

    /**
     * Adds a station to the route.
     * 
     * @param station the station to add
     * @return true if the station was added successfully, false otherwise
     */
    public boolean addStation(Station station) {
        if (station == null) {
            return false;
        }
        return stations.add(station);
    }

    /**
     * Gets the first station in the route.
     * 
     * @return the first station, or null if the route is empty
     */
    public Station getOrigin() {
        if (stations.isEmpty()) {
            return null;
        }
        return stations.get(0);
    }

    /**
     * Gets the last station in the route.
     * 
     * @return the last station, or null if the route is empty
     */
    public Station getDestination() {
        if (stations.isEmpty()) {
            return null;
        }
        return stations.get(stations.size() - 1);
    }
}
