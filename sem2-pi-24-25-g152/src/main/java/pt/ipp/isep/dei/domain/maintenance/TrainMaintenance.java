package pt.ipp.isep.dei.domain.maintenance;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;

import java.util.ArrayList;
import java.util.List;

public class TrainMaintenance {
    private int year;
    private double totalCost;
    private List<Locomotive> locomotives;

    public TrainMaintenance(int year) {
        this.year = year;
        this.locomotives = new ArrayList<>();
    }

    public void calculateFromLocomotives(List<Locomotive> locomotives) {
        // TODO: Implement calculation from locomotives
    }
}
