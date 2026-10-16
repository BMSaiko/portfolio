package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;
import pt.ipp.isep.dei.domain.map.Station;
import pt.ipp.isep.dei.dto.*;
import pt.ipp.isep.dei.repository.IBuildingUpgradeRepository;
import pt.ipp.isep.dei.repository.IStationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.util.List;

public class UpgradeStationController {

    private IStationRepository stationRepository;
    private IBuildingUpgradeRepository buildingUpgradeRepository;

    public UpgradeStationController() {
        Repositories repos = Repositories.getInstance();
        this.stationRepository = repos.getStationRepository();
        this.buildingUpgradeRepository = repos.getBuildingUpgradeRepository();
    }

    public List<StationDTO> getStations() {
        return null;
    }

    public List<AvailableUpgradeDTO> getAvailableUpgrades(String stationId) {
        return null;
    }

    public UpgradeResultDTO applyUpgrade(StationUpgradeRequestDTO request) {
        return null;
    }

    public PlayerDTO getPlayerInfo() {
        return null;
    }

    private boolean validateUpgradeCompatibility(Station station, BuildingUpgrade buildingUpgrade) {
        return false;
    }

    private boolean calculateAffordability(BuildingUpgrade upgrade, double budget) {
        return false;
    }

    private List<String> findConflictingUpgrades(Station station, BuildingUpgrade newUpgrade) {
        return null;
    }
}
