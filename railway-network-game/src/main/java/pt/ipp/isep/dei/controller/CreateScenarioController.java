package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.repository.*;
import pt.ipp.isep.dei.service.AuthenticationService;
import pt.ipp.isep.dei.service.ScenarioService;
import java.util.List;

public class CreateScenarioController {

    private AuthenticationService authService;
    private GameRepository gameRepository;
    private MapRepository mapRepository;
    private LocomotiveTypeRepository locomotiveTypeRepository;
    private IndustryTypeRepository industryTypeRepository;
    private ScenarioRepository scenarioRepository;
    private ScenarioService scenarioService;

    public CreateScenarioController() {
    }

    public Object getCurrentUserRole() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Object> getMapList() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Object> getLocomotiveTypeList() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Object> getIndustryTypeList() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean createScenario(Object map, String name, Object startTime, Object endTime,
                                List<Object> locomotiveTypes, List<Object> industryTypes, List<Object> historicalEvents) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
