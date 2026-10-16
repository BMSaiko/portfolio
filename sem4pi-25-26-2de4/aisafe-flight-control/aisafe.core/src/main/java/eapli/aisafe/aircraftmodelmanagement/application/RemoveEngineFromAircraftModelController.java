package eapli.aisafe.aircraftmodelmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

/**
 * Application-layer controller for US058 – Remove an engine model from an
 * aircraft model.
 *
 * <p>Invariants enforced:
 * <ul>
 *   <li>The aircraft model must exist.</li>
 *   <li>The engine model must exist.</li>
 *   <li>The engine must be currently certified for the aircraft model.</li>
 *   <li>The aircraft model must retain at least one certified engine after removal.</li>
 *   <li>Removal is not possible if any actual Aircraft instance uses this model.</li>
 *   <li>Only BACKOFFICE_OPERATOR or ADMIN may perform this action.</li>
 * </ul>
 */
public class RemoveEngineFromAircraftModelController {

    private final AuthService authService;
    private final AircraftModelRepository aircraftModelRepository;
    private final EngineModelRepository engineModelRepository;
    private final AircraftRepository aircraftRepository;

    public RemoveEngineFromAircraftModelController(
            final AuthService authService,
            final AircraftModelRepository aircraftModelRepository,
            final EngineModelRepository engineModelRepository,
            final AircraftRepository aircraftRepository) {
        this.authService = authService;
        this.aircraftModelRepository = aircraftModelRepository;
        this.engineModelRepository = engineModelRepository;
        this.aircraftRepository = aircraftRepository;
    }

    /**
     * Removes a certified engine model from an existing aircraft model.
     *
     * @param aircraftModelId the composite identity of the target aircraft model
     * @param engineModelId   the composite identity of the engine model to remove
     * @return the updated {@link AircraftModel}
     */
    public AircraftModel removeEngineFromAircraftModel(final AircraftModelId aircraftModelId,
                                                       final EngineModelId engineModelId) {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

        final AircraftModel aircraft = aircraftModelRepository.findById(aircraftModelId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aircraft model not found: " + aircraftModelId));

        if (aircraftRepository.existsByAircraftModelId(aircraftModelId))
            throw new IllegalStateException(
                    "Cannot modify certified engines: there are actual aircraft "
                            + "registered under model '" + aircraftModelId + "'.");

        final EngineModel engine = engineModelRepository.findById(engineModelId)
                .orElseThrow(() -> new IllegalStateException(
                        "Engine model not found: " + engineModelId));

        aircraft.removeCertifiedEngine(engine);

        return aircraftModelRepository.save(aircraft);
    }

    /**
     * Returns all aircraft models — used by the UI to let the operator
     * select which aircraft model to update.
     */
    public Iterable<AircraftModel> allAircraftModels() {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return aircraftModelRepository.findAll();
    }

    /**
     * Returns all engine models — used by the UI to let the operator
     * select which engine to remove.
     */
    public Iterable<EngineModel> allEngineModels() {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return engineModelRepository.findAll();
    }
}
