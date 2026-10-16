package pt.ipp.isep.dei.domain.financial;

import pt.ipp.isep.dei.domain.locomotive.Train;

import java.util.ArrayList;
import java.util.List;

public class FuelExpense {
    private int year;
    private double totalKilometersTraveled;
    private double fuelCostPerKm;
    private double totalCost;
    private List<Train> trains;

    public FuelExpense(int year) {
        this.year = year;
        this.trains = new ArrayList<>();
    }

    public void calculateFromTrains(List<Train> trains) {
        // TODO: Implement calculation from trains
    }
}
