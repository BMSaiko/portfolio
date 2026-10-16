package pt.ipp.isep.dei.domain.financial;

import java.util.Date;

import pt.ipp.isep.dei.domain.maintenance.TrackMaintenance;
import pt.ipp.isep.dei.domain.maintenance.TrainMaintenance;

public class FinancialReport {
    private int year;
    private double totalExpenses;
    private double totalRevenues;
    private double netProfit;
    private Date generatedDate;
    private TrackMaintenance trackMaintenance;
    private TrainMaintenance trainMaintenance;
    private FuelExpense fuelExpense;
    private CargoRevenue cargoRevenue;

    public FinancialReport(int year) {
        this.year = year;
    }

    public void calculateTotalExpenses() {
        // TODO: Implement total expenses calculation
    }

    public void calculateTotalRevenues() {
        // TODO: Implement total revenues calculation
    }

    public void calculateNetProfit() {
        // TODO: Implement net profit calculation
    }

    public void addExpense(double expense) {
        // TODO: Implement expense addition
    }

    public void addRevenue(double revenue) {
        // TODO: Implement revenue addition
    }
}
