package pt.isep.dei.esinf.domain.railway.upgrade;

import pt.isep.dei.esinf.domain.stations.Station;

import java.util.List;

public class CycleInfo {
    private List<Station> stations;
    private List<String> edges;

    public CycleInfo(List<Station> stations, List<String> edges) {
        this.stations = stations;
        this.edges = edges;
    }

    public List<Station> getStations() { return stations; }
    public List<String> getEdges() { return edges; }
}