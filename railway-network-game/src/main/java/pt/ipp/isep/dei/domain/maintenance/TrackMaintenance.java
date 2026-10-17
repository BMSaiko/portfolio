package pt.ipp.isep.dei.domain.maintenance;

import pt.ipp.isep.dei.domain.locomotive.RailwayLine;

import java.util.ArrayList;
import java.util.List;

public class TrackMaintenance {
    private int year;
    private double totalKilometers;
    private double maintenanceCostPerKm;
    private double totalCost;
    private List<RailwayLine> railwayLines;

    public TrackMaintenance(int year) {
        this.year = year;
        this.railwayLines = new ArrayList<>();
    }

    public void calculateTotalCost() {
        // TODO: Implement total cost calculation
    }
}
