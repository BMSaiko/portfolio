package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.locomotive.Train;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository for managing trains.
 */
public class TrainRepository implements ITrainRepository {
    private  List<Train> trains = new ArrayList<>();

    /**
     * Constructor for TrainRepository.
     */
    public TrainRepository() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Finds all trains in the repository.
     * 
     * @return an unmodifiable list of all trains
     */
    public List<Train> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Finds a train by its ID.
     * 
     * @param id the train ID
     * @return the train with the given ID, or null if not found
     */
    public Train findById(String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Saves a train to the repository.
     * 
     * @param train the train to save
     */
    public void save(Train train) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Train> getAllTrains() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Train> getTrainsByYear(int year) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean saveTrainToFleet(Train train) {
     
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Train getTrainById(String trainId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<Train> getPlayerTrains(String playerId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean removeTrainFromFleet(String trainId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
