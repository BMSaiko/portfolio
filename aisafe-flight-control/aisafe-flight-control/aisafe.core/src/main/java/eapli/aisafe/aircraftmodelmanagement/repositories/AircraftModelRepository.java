package eapli.aisafe.aircraftmodelmanagement.repositories;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;

import java.util.Optional;

public interface AircraftModelRepository {

    /**
     * Finds an aircraft model by its composite identity (modelId + makerName).
     */
    Optional<AircraftModel> findById(AircraftModelId id);

    /**
     * Returns all registered aircraft models.
     */
    Iterable<AircraftModel> findAll();

    /**
     * Persists a new aircraft model (or updates an existing one).
     */
    AircraftModel save(AircraftModel aircraftModel);

    Iterable<String> findAllMakers();

    Iterable<AircraftModel> findByMaker(String makerName);
}