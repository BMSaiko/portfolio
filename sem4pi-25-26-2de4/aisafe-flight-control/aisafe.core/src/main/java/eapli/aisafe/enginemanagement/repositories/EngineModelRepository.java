package eapli.aisafe.enginemanagement.repositories;

import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;

import java.util.Optional;

public interface EngineModelRepository {

    /**
     * Finds an engine model by its composite identity (name + makerId).
     */
    Optional<EngineModel> findById(EngineModelId id);

    /**
     * Returns all registered engine models.
     */
    Iterable<EngineModel> findAll();

    /**
     * Returns all engine models of a given type.
     */
    Iterable<EngineModel> findByType(EngineType type);

    /**
     * Persists a new engine model (or updates an existing one).
     */
    EngineModel save(EngineModel engineModel);
}
