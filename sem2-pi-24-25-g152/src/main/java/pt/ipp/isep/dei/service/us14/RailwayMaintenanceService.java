package pt.ipp.isep.dei.service.us14;

import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.RailwayNetwork;
import pt.ipp.isep.dei.domain.us13.Station;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service for finding a maintenance route that passes once through each railway line.
 */
public class RailwayMaintenanceService {
    private final RailwayNetwork network;

    /**
     * Creates a new service with the specified network.
     *
     * @param network the railway network
     */
    public RailwayMaintenanceService(RailwayNetwork network) {
        this.network = network;
    }

    /**
     * Gets a maintenance route that passes once and only once by each railway line.
     *
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of stations representing the route, or an empty list if no route exists
     */
    public List<Station> getMaintenanceRoute(boolean onlyElectrified) {
        // Get possible start stations for the maintenance route
        List<Station> possibleStarts = getPossibleStartStations(onlyElectrified);
        if (possibleStarts.isEmpty()) {
            return Collections.emptyList();
        }

        // Try each possible start station
        for (Station start : possibleStarts) {
            List<Station> route = findMaintenanceRoute(start, null, onlyElectrified);
            if (!route.isEmpty()) {
                return route;
            }
        }

        return Collections.emptyList();
    }

    /**
     * Gets a maintenance route that passes once and only once by each railway line,
     * starting from the specified station and ending at the specified station.
     *
     * @param startStation the station to start from (if null, any valid start station will be used)
     * @param endStation the station to end at (if null, any valid end station will be used)
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of stations representing the route, or an empty list if no route exists
     */
    public List<Station> getMaintenanceRoute(Station startStation, Station endStation, boolean onlyElectrified) {
        if (startStation == null) {
            return getMaintenanceRoute(onlyElectrified);
        }

        return findMaintenanceRoute(startStation, endStation, onlyElectrified);
    }

    /**
     * Gets a list of possible start stations for a maintenance route.
     *
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of possible start stations, or an empty list if no route exists
     */
    public List<Station> getPossibleStartStations(boolean onlyElectrified) {
        List<Station> possibleStarts = new ArrayList<>();

        // Filter lines based on whether they are electrified
        List<RailwayLine> linesToConsider = new ArrayList<>();
        for (RailwayLine line : network.getAllLines()) {
            if (!onlyElectrified || line.isElectrified()) {
                linesToConsider.add(line);
            }
        }

        if (linesToConsider.isEmpty()) {
            return Collections.emptyList();
        }

        // Count the degree (number of connections) for each station
        for (Station station : network.getAllStations()) {
            int degree = countStationDegree(station, linesToConsider);

            // Stations with odd degree or with degree > 0 (if all stations have even degree)
            // are potential start stations for an Eulerian path
            if (degree > 0 && degree % 2 != 0) {
                possibleStarts.add(station);
            }
        }

        // If no stations with odd degree (Eulerian circuit case), add any station with degree > 0
        if (possibleStarts.isEmpty()) {
            for (Station station : network.getAllStations()) {
                int degree = countStationDegree(station, linesToConsider);
                if (degree > 0) {
                    possibleStarts.add(station);
                    break;  // One station is enough in this case
                }
            }
        }

        return possibleStarts;
    }

    /**
     * Gets a list of possible end stations for a maintenance route, given a start station.
     *
     * @param startStation the start station
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of possible end stations, or an empty list if no route exists
     */
    public List<Station> getPossibleEndStations(Station startStation, boolean onlyElectrified) {
        if (startStation == null) {
            return Collections.emptyList();
        }

        List<Station> possibleEnds = new ArrayList<>();

        // Filter lines based on whether they are electrified
        List<RailwayLine> linesToConsider = new ArrayList<>();
        for (RailwayLine line : network.getAllLines()) {
            if (!onlyElectrified || line.isElectrified()) {
                linesToConsider.add(line);
            }
        }

        if (linesToConsider.isEmpty()) {
            return Collections.emptyList();
        }

        // Find stations with odd degree (potential end stations) or any station if all have even degree
        int startDegree = countStationDegree(startStation, linesToConsider);

        // If the start station has odd degree, we need another odd degree station as the end
        if (startDegree % 2 != 0) {
            for (Station station : network.getAllStations()) {
                if (!station.equals(startStation)) {
                    int degree = countStationDegree(station, linesToConsider);
                    if (degree > 0 && degree % 2 != 0) {
                        possibleEnds.add(station);
                    }
                }
            }
        } else {
            // If the start station has even degree, the end can be the same or any other station
            possibleEnds.add(startStation);
            for (Station station : network.getAllStations()) {
                if (!station.equals(startStation)) {
                    int degree = countStationDegree(station, linesToConsider);
                    if (degree > 0) {
                        possibleEnds.add(station);
                    }
                }
            }
        }

        return possibleEnds;
    }

    /**
     * Counts the degree (number of connections) of a station.
     *
     * @param station the station
     * @param lines the lines to consider
     * @return the degree of the station
     */
    private int countStationDegree(Station station, List<RailwayLine> lines) {
        int degree = 0;

        for (RailwayLine line : lines) {
            if (line.getSource().equals(station)) {
                degree++;
            }
            if (line.getDestination().equals(station)) {
                degree++;
            }
        }

        return degree;
    }

    /**
     * Finds a maintenance route that passes once through each railway line,
     * starting from the specified station.
     *
     * @param startStation the station to start from
     * @param endStation the station to end at, or null if any valid end station is acceptable
     * @param onlyElectrified if true, only considers electrified lines; otherwise considers all lines
     * @return a list of stations representing the route, or an empty list if no route exists
     */
    private List<Station> findMaintenanceRoute(Station startStation, Station endStation, boolean onlyElectrified) {
        // Filter lines based on whether they are electrified
        List<RailwayLine> linesToConsider = new ArrayList<>();
        for (RailwayLine line : network.getAllLines()) {
            if (!onlyElectrified || line.isElectrified()) {
                linesToConsider.add(line);
            }
        }

        if (linesToConsider.isEmpty()) {
            return Collections.emptyList();
        }

        // Create a copy of the lines to keep track of which ones have been visited
        List<RailwayLine> remainingLines = new ArrayList<>(linesToConsider);

        // Start building the path from the startStation
        List<Station> path = new ArrayList<>();
        path.add(startStation);

        Station currentStation = startStation;

        // Keep moving until we've used all the lines or can't move further
        while (!remainingLines.isEmpty()) {
            RailwayLine nextLine = findNextLine(currentStation, remainingLines);

            if (nextLine == null) {
                // If we can't find a next line, but haven't used all lines yet,
                // this means we can't create a valid maintenance route
                return Collections.emptyList();
            }

            // Move to the next station
            Station nextStation = nextLine.getSource().equals(currentStation) ?
                    nextLine.getDestination() : nextLine.getSource();

            path.add(nextStation);
            currentStation = nextStation;
            remainingLines.remove(nextLine);

            // If we've specified an end station and we've used all lines,
            // check if we've ended at the specified station
            if (remainingLines.isEmpty() && endStation != null && !currentStation.equals(endStation)) {
                return Collections.emptyList();
            }
        }

        return path;
    }

    /**
     * Finds the next line to use in the maintenance route.
     *
     * @param currentStation the current station
     * @param remainingLines the lines that haven't been used yet
     * @return the next line to use, or null if no line can be used
     */
    private RailwayLine findNextLine(Station currentStation, List<RailwayLine> remainingLines) {
        // First, try to find a bridge edge (removing it would disconnect the graph)
        for (RailwayLine line : remainingLines) {
            if (line.getSource().equals(currentStation) || line.getDestination().equals(currentStation)) {
                List<RailwayLine> remainingAfterLine = new ArrayList<>(remainingLines);
                remainingAfterLine.remove(line);

                if (isBridge(line, remainingAfterLine)) {
                    return line;
                }
            }
        }

        // If no bridge edge, just return any available edge
        for (RailwayLine line : remainingLines) {
            if (line.getSource().equals(currentStation) || line.getDestination().equals(currentStation)) {
                return line;
            }
        }

        return null;
    }

    /**
     * Checks if a line is a bridge (removing it would disconnect the graph).
     *
     * @param line the line to check
     * @param remainingLines the remaining lines in the graph
     * @return true if the line is a bridge, false otherwise
     */
    private boolean isBridge(RailwayLine line, List<RailwayLine> remainingLines) {
        // If there are no other lines, this is a bridge by definition
        if (remainingLines.isEmpty()) {
            return true;
        }

        // Create an adjacency map for the remaining lines
        Set<Station> stations = new HashSet<>();
        for (RailwayLine l : remainingLines) {
            stations.add(l.getSource());
            stations.add(l.getDestination());
        }

        // Check if the remaining graph is connected
        Set<Station> visited = new HashSet<>();
        dfs(remainingLines.get(0).getSource(), remainingLines, visited);

        // If all stations can be visited, the graph is still connected
        return visited.size() != stations.size();
    }

    /**
     * Performs a depth-first search of the graph.
     *
     * @param current the current station
     * @param lines the lines in the graph
     * @param visited the set of visited stations
     */
    private void dfs(Station current, List<RailwayLine> lines, Set<Station> visited) {
        visited.add(current);

        for (RailwayLine line : lines) {
            if (line.getSource().equals(current) && !visited.contains(line.getDestination())) {
                dfs(line.getDestination(), lines, visited);
            } else if (line.getDestination().equals(current) && !visited.contains(line.getSource())) {
                dfs(line.getSource(), lines, visited);
            }
        }
    }
}