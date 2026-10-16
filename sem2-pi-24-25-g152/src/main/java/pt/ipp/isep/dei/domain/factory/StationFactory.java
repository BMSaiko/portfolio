package pt.ipp.isep.dei.domain.factory;
import pt.ipp.isep.dei.domain.map.Map;
import pt.ipp.isep.dei.domain.scenario.Station;

public interface StationFactory {
    Station createStation(int x, int y, String name, Map map);
}
