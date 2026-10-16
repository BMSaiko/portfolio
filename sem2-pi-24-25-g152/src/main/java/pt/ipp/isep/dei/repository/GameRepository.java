package pt.ipp.isep.dei.repository;


public interface GameRepository {
    MapRepository getMapRepository();
    StationRepository getStationRepository(); // Added based on US08
}
