package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.dto.TrainDTO;
import pt.ipp.isep.dei.repository.TrainRepository;
import pt.ipp.isep.dei.service.TrainService;
import java.util.List;

/**
 * Controller for train operations.
 */
public class TrainController {
    private final TrainService trainService;

    /**
     * Constructor for TrainController.
     * 
     * @param trainService the train service
     */
    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }
    
    /**
     * Default constructor for TrainController.
     * Creates a new TrainService with a new TrainRepository.
     */
    public TrainController() {
        this.trainService = new TrainService(new TrainRepository());
    }

    /**
     * Lists all trains grouped by locomotive type.
     * 
     * @return a response containing the list of trains
     */
    public Response<List<TrainDTO>> listAllTrains() {
      return Response.ok(trainService.getAllTrainsGrouped());
    }
}
