package eapli.aisafe.aircraftmodelmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

/**
 * Application-layer controller for US057 – Add an engine model to an aircraft model.
 *
 * <p>Invariants enforced:
 * <ul>
 *   <li>The aircraft model must exist.</li>
 *   <li>The engine model must exist.</li>
 *   <li>The engine type must be compatible with the aircraft's existing certified engines.</li>
 *   <li>The engine model must not already be certified for the aircraft model.</li>
 *   <li>Only BACKOFFICE_OPERATOR or ADMIN may perform this action.</li>
 * </ul>
 */
public class AddEngineToAircraftModelController {

    private final AuthService authService;
    private final AircraftModelRepository aircraftModelRepository;
    private final EngineModelRepository engineModelRepository;

    public AddEngineToAircraftModelController(final AuthService authService,
                                              final AircraftModelRepository aircraftModelRepository,
                                              final EngineModelRepository engineModelRepository) {
        this.authService = authService;
        this.aircraftModelRepository = aircraftModelRepository;
        this.engineModelRepository = engineModelRepository;
    }

    /**
     * Adds a certified engine model to an existing aircraft model.
     *
     * @param aircraftModelId the composite identity of the target aircraft model
     * @param engineModelId   the composite identity of the engine model to add
     * @return the updated {@link AircraftModel}
     */
    public AircraftModel addEngineToAircraftModel(final AircraftModelId aircraftModelId,
                                                  final EngineModelId engineModelId) {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

        final AircraftModel aircraft = aircraftModelRepository.findById(aircraftModelId)
                .orElseThrow(() -> new IllegalStateException(
                        "Aircraft model not found: " + aircraftModelId));

        final EngineModel engine = engineModelRepository.findById(engineModelId)
                .orElseThrow(() -> new IllegalStateException(
                        "Engine model not found: " + engineModelId));

        aircraft.addCertifiedEngine(engine);

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
     * select which engine to add.
     */
    public Iterable<EngineModel> allEngineModels() {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return engineModelRepository.findAll();
    }
}
