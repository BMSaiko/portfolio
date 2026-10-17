package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.stationtype.StationType;
import pt.ipp.isep.dei.dto.PlayerDTO;
import pt.ipp.isep.dei.dto.StationCreationRequestDTO;
import pt.ipp.isep.dei.dto.StationResponseDTO;
import pt.ipp.isep.dei.dto.ValidationResultDTO;
import pt.ipp.isep.dei.repository.IMapRepository;
import pt.ipp.isep.dei.repository.IStationRepository;
import pt.ipp.isep.dei.repository.Repositories;
import pt.ipp.isep.dei.domain.map.Map;

public class BuildStationController {


    public StationType[] getStationTypes() { return null; }
    public boolean validatePosition(int x, int y, StationType stationType) { return false; }
    public void buildStation(int x, int y, StationType stationType, String stationName) { }
    public double calculateStationCost(StationType stationType) { return 0.0; }

    private IMapRepository mapRepository;
    private IStationRepository stationRepository;

    public BuildStationController() {
        Repositories repos = Repositories.getInstance();
        this.mapRepository = repos.getMapRepository();
        this.stationRepository = repos.getStationRepository();
    }

    public java.util.List<String> getAvailableStationTypes() {
        // Implementation logic goes here
        return null;
    }

    public Map getCurrentMap() {
        return mapRepository.getCurrentMap();
    }

    public ValidationResultDTO validatePosition(StationCreationRequestDTO request) {
        // Implementation logic goes here
        return null;
    }

    public String generateStationName(int x, int y) {
        // Implementation logic goes here
        return null;
    }

    public StationResponseDTO buildStation(StationCreationRequestDTO request) {
        // Implementation logic goes here
        return null;
    }

    public PlayerDTO getPlayerInfo() {
        // Implementation logic goes here
        return null;
    }

    private boolean canAffordStation(String stationType, double budget) {
        // Implementation logic goes here
        return true;
    }
}
