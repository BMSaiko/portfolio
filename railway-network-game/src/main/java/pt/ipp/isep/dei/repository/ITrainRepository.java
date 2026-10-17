package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.locomotive.Train;

import java.util.List;

public interface ITrainRepository {
    boolean saveTrainToFleet(Train train);
    Train getTrainById(String trainId);
    List<Train> getPlayerTrains(String playerId);
    boolean removeTrainFromFleet(String trainId);
}
