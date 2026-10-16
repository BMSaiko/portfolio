package pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;
import pt.ipp.isep.dei.domain.locomotive.RailwayLine;
import pt.ipp.isep.dei.domain.map.Station;



public class Player {
    private String name;
    private double funds;
    private int id; 
    private List<RailwayLine> railwayLines;
    private List<Station> stations;
    private List<Train> trains;



    public Player(String name, double initialFunds, int id, List<Station> stations) {
        this.name = name;
        this.funds = initialFunds;
        this.railwayLines = new ArrayList<>();
        this.id = id;
        this.stations = stations;
    }

    public String getName() {
        return this.name;
    }

    public double getFunds() {
        return this.funds;
    }

    public List<RailwayLine> getRailwayLines() {
        return this.railwayLines;
    }

    public boolean hasSufficientFunds(double cost) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RailwayLine createRailwayLine(Station startStation, Station endStation, Path path, double cost) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deductFunds(double cost) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFunds(double funds) {
        this.funds = funds;
    }

    public void setRailwayLines(List<RailwayLine> railwayLines) {
        this.railwayLines = railwayLines;
    }

      public void assignTrainToRoute(Train train, Route route) {
        train.setAssignedRoute(route);
    }

    public List<Station> listStations() {
        return stations;
    }

     public Station selectStation(int stationId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public void updateFunds(double amount) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Train addLocomotive(Locomotive locomotive) {
        return null;
    }

    public boolean canAfford(double amount) {
        return false;
    }

    public int getTrainCount() {
        return 0;
    }
}
