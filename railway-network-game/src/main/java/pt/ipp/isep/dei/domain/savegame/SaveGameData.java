package pt.ipp.isep.dei.domain.savegame;

import pt.ipp.isep.dei.domain.Player;
import pt.ipp.isep.dei.domain.scenario.Scenario;
import pt.ipp.isep.dei.domain.locomotive.Train;
import pt.ipp.isep.dei.domain.map.Station;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data structure representing complete game save data
 */
public class SaveGameData {
    private SaveGameMetadata metadata;
    private Player player;
    private Scenario scenario;
    private LocalDateTime simulationTime;
    private List<Train> trains;
    private List<Station> stations;
    private GameProgress gameProgress;

    public SaveGameData(SaveGameMetadata metadata, Player player, Scenario scenario) {
        this.metadata = metadata;
        this.player = player;
        this.scenario = scenario;
        this.simulationTime = LocalDateTime.now();
        this.gameProgress = new GameProgress();
    }

    // Getters
    public SaveGameMetadata getMetadata() {
        return metadata;
    }

    public Player getPlayer() {
        return player;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public LocalDateTime getSimulationTime() {
        return simulationTime;
    }

    public List<Train> getTrains() {
        return trains;
    }

    public List<Station> getStations() {
        return stations;
    }

    public GameProgress getGameProgress() {
        return gameProgress;
    }

    // Setters
    public void setMetadata(SaveGameMetadata metadata) {
        this.metadata = metadata;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void setSimulationTime(LocalDateTime simulationTime) {
        this.simulationTime = simulationTime;
    }

    public void setTrains(List<Train> trains) {
        this.trains = trains;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public void setGameProgress(GameProgress gameProgress) {
        this.gameProgress = gameProgress;
    }
}
