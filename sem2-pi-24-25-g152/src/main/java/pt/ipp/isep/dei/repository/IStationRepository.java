package pt.ipp.isep.dei.repository;
import pt.ipp.isep.dei.domain.map.Station;

public interface IStationRepository {
    void addStation(Station station);
    java.util.List<Station> getStations();
    Station getStationById(int id);
    boolean removeStation(int id);
    Station getStationById(String stationId);
    void updateStation(Station station);
    java.util.List<Station> getStationsByType(String stationType);
}
