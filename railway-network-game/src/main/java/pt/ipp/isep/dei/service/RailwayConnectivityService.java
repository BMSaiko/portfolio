package pt.ipp.isep.dei.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.RailwayNetwork;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.domain.us13.StationType;
import pt.ipp.isep.dei.domain.us13.TrainType;

/**
 * Service for checking railway connectivity.
 */
public class RailwayConnectivityService {
    private final RailwayNetwork network;
    
    /**
     * Creates a new railway connectivity service.
     * 
     * @param network the railway network
     */
    public RailwayConnectivityService(RailwayNetwork network) {
        this.network = network;
    }
    
    /**
     * Checks if a train of the specified type can travel between two stations.
     * 
     * @param source the source station
     * @param destination the destination station
     * @param trainType the type of train
     * @return true if the train can travel between the stations, false otherwise
     */
    public boolean canTravel(Station source, Station destination, TrainType trainType) {
        if (source.equals(destination)) {
            return true; // Same station, no travel needed
        }
        
        // BFS to find a path from source to destination
        Queue<Station> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();
        Map<Station, Station> previous = new HashMap<>();
        
        queue.add(source);
        visited.add(source);
        
        while (!queue.isEmpty()) {
            Station current = queue.poll();
            
            for (RailwayLine line : network.getLinesForStation(current)) {
                // Check if the train can operate on this line
                if (!trainType.canOperateOn(line)) {
                    continue;
                }
                
                Station next = line.getOtherStation(current);
                
                // If we haven't visited this station yet
                if (!visited.contains(next)) {
                    visited.add(next);
                    previous.put(next, current);
                    queue.add(next);
                    
                    // If we've reached the destination
                    if (next.equals(destination)) {
                        return true;
                    }
                }
            }
        }
        
        // No path found
        return false;
    }
    
    /**
     * Finds all stations of a specific type that can be reached from the source station
     * using a train of the specified type.
     * 
     * @param source the source station
     * @param stationType the type of stations to find
     * @param trainType the type of train
     * @return a list of reachable stations of the specified type
     */
    public List<Station> findReachableStationsOfType(Station source, StationType stationType, TrainType trainType) {
        List<Station> reachableStations = new ArrayList<>();
        
        // BFS to find all reachable stations
        Queue<Station> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();
        
        queue.add(source);
        visited.add(source);
        
        while (!queue.isEmpty()) {
            Station current = queue.poll();
            
            // If this station is of the specified type and not the source station
            if (current.getType() == stationType && !current.equals(source)) {
                reachableStations.add(current);
            }
            
            for (RailwayLine line : network.getLinesForStation(current)) {
                // Check if the train can operate on this line
                if (!trainType.canOperateOn(line)) {
                    continue;
                }
                
                Station next = line.getOtherStation(current);
                
                // If we haven't visited this station yet
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        
        return reachableStations;
    }
    
    /**
     * Gets the path between two stations for a train of the specified type.
     * 
     * @param source the source station
     * @param destination the destination station
     * @param trainType the type of train
     * @return the path as a list of stations, or an empty list if no path exists
     */
    public List<Station> getPath(Station source, Station destination, TrainType trainType) {
        if (source.equals(destination)) {
            return List.of(source); // Same station, no travel needed
        }
        
        // BFS to find a path from source to destination
        Queue<Station> queue = new LinkedList<>();
        Set<Station> visited = new HashSet<>();
        Map<Station, Station> previous = new HashMap<>();
        
        queue.add(source);
        visited.add(source);
        
        boolean pathFound = false;
        
        while (!queue.isEmpty() && !pathFound) {
            Station current = queue.poll();
            
            for (RailwayLine line : network.getLinesForStation(current)) {
                // Check if the train can operate on this line
                if (!trainType.canOperateOn(line)) {
                    continue;
                }
                
                Station next = line.getOtherStation(current);
                
                // If we haven't visited this station yet
                if (!visited.contains(next)) {
                    visited.add(next);
                    previous.put(next, current);
                    queue.add(next);
                    
                    // If we've reached the destination
                    if (next.equals(destination)) {
                        pathFound = true;
                        break;
                    }
                }
            }
        }
        
        if (!pathFound) {
            return Collections.emptyList(); // No path found
        }
        
        // Reconstruct the path
        List<Station> path = new ArrayList<>();
        Station current = destination;
        
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }
        
        // Reverse the path to get it from source to destination
        Collections.reverse(path);
        
        return path;
    }
}