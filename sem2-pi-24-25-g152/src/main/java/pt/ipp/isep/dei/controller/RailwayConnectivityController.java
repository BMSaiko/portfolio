package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.*;
import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.RailwayNetwork;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.domain.us13.StationType;
import pt.ipp.isep.dei.domain.us13.TrainType;
import pt.ipp.isep.dei.repository.RailwayNetworkRepository;
import pt.ipp.isep.dei.service.RailwayConnectivityService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for US13 - checking if a specific train can travel between stations.
 */
public class RailwayConnectivityController {
    private final RailwayNetwork network;
    private final RailwayConnectivityService connectivityService;
    
    /**
     * Creates a new controller with the specified network repository.
     * 
     * @param stationsFile the path to the stations CSV file
     * @param linesFile the path to the lines CSV file
     * @throws IOException if an I/O error occurs while loading the network
     */
    public RailwayConnectivityController(String stationsFile, String linesFile) throws IOException {
        RailwayNetworkRepository repository = new RailwayNetworkRepository();
        this.network = repository.loadFromCsv(stationsFile, linesFile);
        this.connectivityService = new RailwayConnectivityService(network);
    }
    
    /**
     * Gets all stations in the network.
     * 
     * @return a list of all stations
     */
    public List<Station> getAllStations() {
        return new ArrayList<>(network.getAllStations());
    }
    
    /**
     * Gets all stations of a specific type in the network.
     * 
     * @param type the station type
     * @return a list of stations of the specified type
     */
    public List<Station> getStationsByType(StationType type) {
        return network.getAllStations().stream()
                .filter(station -> station.getType() == type)
                .collect(Collectors.toList());
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
        return connectivityService.canTravel(source, destination, trainType);
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
        return connectivityService.getPath(source, destination, trainType);
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
        return connectivityService.findReachableStationsOfType(source, stationType, trainType);
    }
    
    /**
     * Gets all railway lines in the network.
     * 
     * @return a list of all railway lines
     */
    public List<RailwayLine> getAllLines() {
        return network.getAllLines();
    }
}