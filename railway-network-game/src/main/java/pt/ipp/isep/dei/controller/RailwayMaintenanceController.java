package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.RailwayNetwork;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.repository.RailwayNetworkRepository;
import pt.ipp.isep.dei.service.us14.RailwayMaintenanceService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for US14 - finding a maintenance route that passes once and only once
 * through each railway line.
 */
public class RailwayMaintenanceController {
    private final RailwayNetwork network;
    private final RailwayMaintenanceService maintenanceService;

    /**
     * Creates a new controller with the specified network repository.
     *
     * @param stationsFile the path to the stations CSV file
     * @param linesFile the path to the lines CSV file
     * @throws IOException if an I/O error occurs while loading the network
     */
    public RailwayMaintenanceController(String stationsFile, String linesFile) throws IOException {
        RailwayNetworkRepository repository = new RailwayNetworkRepository();
        this.network = repository.loadFromCsv(stationsFile, linesFile);
        this.maintenanceService = new RailwayMaintenanceService(network);
    }

    /**
     * Gets a maintenance route that passes once and only once by each railway line.
     *
     * @param startStation the station to start from (if null, any valid start station will be used)
     * @param endStation the station to end at (if null, any valid end station will be used)
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of stations representing the route, or an empty list if no route exists
     */
    public List<Station> getMaintenanceRoute(Station startStation, Station endStation, boolean onlyElectrified) {
        return maintenanceService.getMaintenanceRoute(startStation, endStation, onlyElectrified);
    }

    /**
     * Gets a maintenance route that passes once and only once by each railway line.
     * This is a convenience method that doesn't specify start or end stations.
     *
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of stations representing the route, or an empty list if no route exists
     */
    public List<Station> getMaintenanceRoute(boolean onlyElectrified) {
        return maintenanceService.getMaintenanceRoute(null, null, onlyElectrified);
    }

    /**
     * Gets a list of possible start stations for a maintenance route.
     *
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of possible start stations, or an empty list if no route exists
     */
    public List<Station> getPossibleStartStations(boolean onlyElectrified) {
        return maintenanceService.getPossibleStartStations(onlyElectrified);
    }

    /**
     * Gets a list of possible end stations for a maintenance route, given a start station.
     *
     * @param startStation the start station
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of possible end stations, or an empty list if no route exists
     */
    public List<Station> getPossibleEndStations(Station startStation, boolean onlyElectrified) {
        return maintenanceService.getPossibleEndStations(startStation, onlyElectrified);
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
     * Gets all lines in the network.
     *
     * @return a list of all railway lines
     */
    public List<RailwayLine> getAllLines() {
        return network.getAllLines();
    }
}