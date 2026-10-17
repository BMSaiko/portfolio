package pt.ipp.isep.dei.controller;

import java.util.List;
import java.util.Optional;

import pt.ipp.isep.dei.domain.Path;
import pt.ipp.isep.dei.domain.Player; // Assuming this exists or needs creation
import pt.ipp.isep.dei.domain.map.Station;
import pt.ipp.isep.dei.repository.GameRepository;
import pt.ipp.isep.dei.repository.RailwayLineRepository;
import pt.ipp.isep.dei.repository.StationRepository;
import pt.ipp.isep.dei.service.GameSession;
import pt.ipp.isep.dei.service.PathFinderService;
import pt.ipp.isep.dei.service.RailwayLineService;

public class BuildRailwayLineController {

    private GameSession gameSession;
    private GameRepository gameRepository;
    private StationRepository stationRepository;
    private PathFinderService pathFinderService;
    private RailwayLineService railwayLineService;
    private RailwayLineRepository railwayLineRepository;

    public BuildRailwayLineController() {
    }

    public Player getCurrentPlayer() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Station> getAvailableStations() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Station> getAvailableEndStations(Station startStation) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public Optional<Object[]> calculatePathAndCost(Station startStation, Station endStation) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean buildRailwayLine(Player player, Station startStation, Station endStation, Path path, double cost) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
