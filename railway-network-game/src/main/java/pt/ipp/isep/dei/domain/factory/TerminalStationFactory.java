package pt.ipp.isep.dei.domain.factory;
import pt.ipp.isep.dei.domain.map.Map;
import pt.ipp.isep.dei.domain.scenario.Station;
import pt.ipp.isep.dei.domain.scenario.TerminalStation;

public class TerminalStationFactory implements StationFactory {

    @Override
    public Station createStation(int x, int y, String name, Map map) {
        return new TerminalStation(x, y, name, map);
    }
}
