package pt.ipp.isep.dei.domain.locomotive;


import pt.ipp.isep.dei.domain.Path;
import pt.ipp.isep.dei.domain.map.Station;

public class RailwayLine {
    private Station startStation;
    private Station endStation;
    private Path path;
    private double cost;
    private double length;
    private String type;
    private boolean electrified;
    private double maintenanceCost;

    public RailwayLine(Station startStation, Station endStation, Path path, double cost) {
        this.startStation = startStation;
        this.endStation = endStation;
        this.path = path;
        this.cost = cost;
    }

    public boolean validateData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public Path getPath() {
        return path;
    }

    public double getCost() {
        return cost;
    }

    public void setStartStation(Station startStation) {
        this.startStation = startStation;
    }

    public void setEndStation(Station endStation) {
        this.endStation = endStation;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
