package pt.ipp.isep.dei.domain.us13;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Represents a railway network with stations and lines.
 */
public class RailwayNetwork {
    private final Map<String, Station> stations = new HashMap<>();
    private final List<RailwayLine> lines = new ArrayList<>();
    
    /**
     * Adds a station to the network.
     * 
     * @param station the station to add
     */
    public void addStation(Station station) {
        stations.put(station.getName(), station);
    }
    
    /**
     * Gets a station by its name.
     * 
     * @param name the name of the station
     * @return the station with the given name
     * @throws NoSuchElementException if no station with the given name exists
     */
    public Station getStation(String name) {
        Station station = stations.get(name);
        if (station == null) {
            throw new NoSuchElementException("No station with name: " + name);
        }
        return station;
    }
    
    /**
     * Adds a railway line to the network.
     * 
     * @param line the line to add
     */
    public void addLine(RailwayLine line) {
        lines.add(line);
    }
    
    /**
     * Gets all stations in the network.
     * 
     * @return a collection of all stations
     */
    public Collection<Station> getAllStations() {
        return Collections.unmodifiableCollection(stations.values());
    }
    
    /**
     * Gets all lines in the network.
     * 
     * @return a list of all lines
     */
    public List<RailwayLine> getAllLines() {
        return Collections.unmodifiableList(lines);
    }
    
    /**
     * Gets all lines connected to a station.
     * 
     * @param station the station
     * @return a list of all lines connected to the station
     */
    public List<RailwayLine> getLinesForStation(Station station) {
        List<RailwayLine> connectedLines = new ArrayList<>();
        for (RailwayLine line : lines) {
            if (line.getSource().equals(station) || line.getDestination().equals(station)) {
                connectedLines.add(line);
            }
        }
        return connectedLines;
    }
}