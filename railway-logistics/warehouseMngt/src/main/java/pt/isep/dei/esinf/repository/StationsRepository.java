package pt.isep.dei.esinf.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.isep.dei.esinf.domain.stations.CoordinateStationKey;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.domain.stations.TimeZone;
import pt.isep.dei.esinf.utils.BST;
import pt.isep.dei.esinf.utils.KDTree;

public class StationsRepository {

    private static StationsRepository instance = null;

    private BST<TimeZone> stationsBST = new BST<>();
    private BST<CoordinateStationKey> coordinatesBST = new BST<>();
    private KDTree<Station> kdTree = new KDTree<>();

    private Map<TimeZone, List<Station>> stations = new HashMap<>();

    public static StationsRepository getInstance() {
        if (instance == null) {
            instance = new StationsRepository();
        }
        return instance;
    }

    public Map<TimeZone, List<Station>> getStations() {
        return stations;
    }

    public void setStations(Map<TimeZone, List<Station>> stations) {
        this.stations = stations;
    }

    public BST<TimeZone> getStationsBST() {
        return stationsBST;
    }

    public void setStationsBST(BST<TimeZone> stationsBST) {
        this.stationsBST = stationsBST;
    }

    public BST<CoordinateStationKey> getCoordinatesBST() {
        return coordinatesBST;
    }

    public void setCoordinatesBST(BST<CoordinateStationKey> coordinatesBST) {
        this.coordinatesBST = coordinatesBST;
    }

    public KDTree<Station> getKdTree() {
        return kdTree;
    }

    /**
     * Adds a station to all relevant search structures
     * @param timeZone the time zone of the station
     * @param station the station to add
     */
    public void addStation(TimeZone timeZone, Station station) {
        if (!station.isValid()) {
            throw new IllegalArgumentException("Invalid station: " + station.getName());
        }

        stations.computeIfAbsent(timeZone, k -> new ArrayList<>()).add(station);

        kdTree.insert(station);
        stationsBST.insert(timeZone);
        coordinatesBST.insert(new CoordinateStationKey(station.getCoordinates(), station.getName(), station));
    }

    /**
     * Clears all data structures.
     */
    public void clear() {
        stations.clear();
        stationsBST = new BST<>();
        coordinatesBST = new BST<>();
        kdTree = new KDTree<>();
    }
}
