package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.financial.CargoRevenue;
import pt.ipp.isep.dei.domain.financial.FinancialReport;
import pt.ipp.isep.dei.domain.financial.FuelExpense;
import pt.ipp.isep.dei.domain.maintenance.TrackMaintenance;
import pt.ipp.isep.dei.domain.maintenance.TrainMaintenance;
import pt.ipp.isep.dei.session.UserSession;

import java.util.List;


public class ViewFinancialResultsController {

    public ViewFinancialResultsController() {
        // TODO: Initialize controller
    }

    public FinancialReport getYearFinancialResults(int year) {
        // TODO: Implement financial results retrieval for year
        return null;
    }

    public List<Integer> getAvailableYears() {
        // TODO: Implement available years retrieval
        return null;
    }

    private TrackMaintenance calculateTrackMaintenance(int year) {
        // TODO: Implement track maintenance calculation
        return null;
    }

    private TrainMaintenance calculateTrainMaintenance(int year) {
        // TODO: Implement train maintenance calculation
        return null;
    }

    private FuelExpense calculateFuelExpenses(int year) {
        // TODO: Implement fuel expenses calculation
        return null;
    }

    private CargoRevenue calculateCargoRevenues(int year) {
        // TODO: Implement cargo revenues calculation
        return null;
    }

    private UserSession getPlayerFromSession() {
        // TODO: Implement player retrieval from session
        return null;
    }
}
