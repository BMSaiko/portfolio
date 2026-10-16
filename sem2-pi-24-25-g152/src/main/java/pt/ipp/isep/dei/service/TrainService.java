package pt.ipp.isep.dei.service;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;
import pt.ipp.isep.dei.domain.locomotive.Train;
import pt.ipp.isep.dei.dto.CargoDTO;
import pt.ipp.isep.dei.dto.RouteDTO;
import pt.ipp.isep.dei.dto.TrainDTO;
import pt.ipp.isep.dei.repository.TrainRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for train operations.
 */
public class TrainService {
    private final TrainRepository trainRepository;
    
    /**
     * Constructor for TrainService.
     * 
     * @param trainRepository the train repository
     */
    public TrainService(TrainRepository trainRepository) {
    
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets all trains grouped by locomotive type and sorted alphabetically within groups.
     * 
     * @return a list of train DTOs grouped by locomotive type
     */
    public List<TrainDTO> getAllTrainsGrouped() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Groups trains by locomotive type and sorts them alphabetically within groups.
     * 
     * @param trains the list of trains
     * @return a list of train DTOs grouped by locomotive type
     */
    private List<TrainDTO> groupAndSortTrains(List<Train> trains) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Converts a Train entity to a TrainDTO.
     * 
     * @param train the train entity
     * @return the train DTO
     */
    private TrainDTO convertToDTO(Train train) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
