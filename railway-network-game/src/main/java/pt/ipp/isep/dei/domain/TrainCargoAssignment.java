package pt.ipp.isep.dei.domain;

import pt.ipp.isep.dei.domain.locomotive.Cargo;
import pt.ipp.isep.dei.domain.map.Station;

public class TrainCargoAssignment {
    private Cargo cargo;
    private Station station;
    private int quantity;

    public TrainCargoAssignment(Cargo cargo, Station station, int quantity) {
        this.cargo = cargo;
        this.station = station;
        this.quantity = quantity;
    }

    public String getDescription() {
        return quantity + " units of " + cargo.getType() + " from " + station.getName();
    }
// Getters
    public Cargo getCargo() {
        return cargo;
    }

    public Station getStation() {
        return station;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters
    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}