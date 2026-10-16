package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.map.Station;

import java.util.List;
import java.util.Optional;

public class StationRepository  implements IStationRepository {
    private java.util.List<Station> stations;

    List<Station> getAllStations() {
        return null;
    }

    List<Station> getPossibleEndStations(Station startStation) {
        return null;
    }

    Optional<Station> getStationByName(String name) {
        return null;
    }

    public StationRepository() {
        this.stations = new java.util.ArrayList<>();
    }

    @Override
    public void addStation(Station station) {
        stations.add(station);
    }

    @Override
    public java.util.List<Station> getStations() {
        return new java.util.ArrayList<>(stations);
    }

    @Override
    public Station getStationById(int id) {
        return stations.stream()
                .filter(station -> station.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean removeStation(int id) {
        return stations.removeIf(station -> station.getId() == id);
    }

    @Override
    public Station getStationById(String stationId) {
        return null;
    }

    @Override
    public void updateStation(Station station) {
    }

    @Override
    public List<Station> getStationsByType(String stationType) {
        return null;
    }
}





