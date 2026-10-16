package pt.ipp.isep.dei.domain;

import pt.ipp.isep.dei.domain.TrainCargoAssignment;
import pt.ipp.isep.dei.domain.locomotive.Carriage;
import pt.ipp.isep.dei.domain.locomotive.Locomotive;

import java.util.List;

public class Train {
    private int id;
    private String name;
    private int capacity;
    private List<TrainCargoAssignment> cargoAssignments;
    private Route assignedRoute;
    private double kmTraveled;
    private Locomotive locomotive;
    private List<Carriage> carriages;

    public Train(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public Route getAssignedRoute() {
        return assignedRoute;
    }

    public void setAssignedRoute(Route route) {
        this.assignedRoute = route;
    }

    public List<TrainCargoAssignment> getCargoManifest() {
        return cargoAssignments;
    }

    public Train(String id, Locomotive locomotive) {
    }

    public boolean addCarriage(Carriage carriage) {
        return false;
    }

    public Locomotive getLocomotive() {
        return null;
    }

    public List<Carriage> getCarriages() {
        return null;
    }

    public double getTotalWeight() {
        return 0.0;
    }

    public double getMaxSpeed() {
        return 0.0;
    }
}