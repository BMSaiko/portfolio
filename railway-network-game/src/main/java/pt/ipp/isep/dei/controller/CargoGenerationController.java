package pt.ipp.isep.dei.controller;

import java.util.List;

import pt.ipp.isep.dei.dto.CargoGenerationResultDTO;
import pt.ipp.isep.dei.dto.StationDTO;
import pt.ipp.isep.dei.mapper.CargoGenerationMapper;
import pt.ipp.isep.dei.mapper.CargoMapper;
import pt.ipp.isep.dei.mapper.StationMapper;
import pt.ipp.isep.dei.repository.StationRepository;
import pt.ipp.isep.dei.service.CargoDistributionService;
import pt.ipp.isep.dei.service.CityService;
import pt.ipp.isep.dei.service.IndustryService;
import pt.ipp.isep.dei.service.SimulationService;

public class CargoGenerationController {
    private SimulationService simulationService;
    private StationRepository stationRepository;
    private IndustryService industryService;
    private CityService cityService;
    private CargoDistributionService cargoDistributionService;
    private StationMapper stationMapper;
    private CargoMapper cargoMapper;
    private CargoGenerationMapper cargoGenerationMapper;

    public List<StationDTO> getStations() {
        // TODO: Implement logic
        return null;
    }

    public CargoGenerationResultDTO startCargoGeneration() {
        // TODO: Implement logic
        return null;
    }
}
