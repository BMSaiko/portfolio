package pt.ipp.isep.dei.ui;

import pt.ipp.isep.dei.controller.CreateScenarioController;
import pt.ipp.isep.dei.domain.locomotive.LocomotiveType;
import pt.ipp.isep.dei.domain.map.Map;
import pt.ipp.isep.dei.domain.scenario.HistoricalEvent;
import pt.ipp.isep.dei.domain.scenario.IndustryType;

import java.util.Date;
import java.util.List;

public class CreateScenarioUI implements Runnable {

    private final CreateScenarioController controller = new CreateScenarioController();

    public CreateScenarioUI() {
        // Empty constructor
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void createScenario() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private boolean validateRole(String role) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private void saveSelectedMap(Map map) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private void saveData(String name, Date startTime, Date endTime) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private void saveSelectedLocomotiveTypes(List<LocomotiveType> types) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private void saveSelectedIndustryTypes(List<IndustryType> types) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private void saveHistoricalEvents(List<HistoricalEvent> events) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
