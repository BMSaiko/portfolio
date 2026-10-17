package eapli.aisafe.enginemanagement.application;

import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

/**
 * Application-layer controller for US056 – Create an aircraft engine model.
 *
 * <p>Invariants enforced:
 * <ul>
 *   <li>The (name + makerId) combination must be unique.</li>
 *   <li>All physical parameters must be valid (positive, physically coherent).</li>
 *   <li>Fuel type must be one of the pre-defined {@link FuelType} values.</li>
 *   <li>Only BACKOFFICE_OPERATOR or ADMIN may perform this action.</li>
 * </ul>
 */
public class RegisterEngineModelController {

    private final AuthService authService;
    private final EngineModelRepository repository;

    public RegisterEngineModelController(final AuthService authService,
                                         final EngineModelRepository repository) {
        this.authService = authService;
        this.repository = repository;
    }

    /**
     * Registers a new engine model.
     *
     * @param name                model name (e.g. "GE 90B")
     * @param makerId             maker identifier (e.g. "GE")
     * @param engineType          one of the {@link EngineType} values
     * @param thrustAtStandstill  thrust at sea level / standstill (N)
     * @param thrustAtCruiseSpeed thrust at cruise speed (N)
     * @param cruiseSpeedMach     cruise speed at which thrustAtCruiseSpeed was measured (Mach)
     * @param tsfc                Thrust Specific Fuel Consumption (N/N/s, SI)
     * @param lapseRateFactor     altitude lapse-rate exponent
     * @param fuelType            one of the {@link FuelType} values
     * @return the persisted {@link EngineModel}
     */
    public EngineModel registerEngineModel(
            final String name,
            final String makerId,
            final String engineType,
            final double thrustAtStandstill,
            final double thrustAtCruiseSpeed,
            final double cruiseSpeedMach,
            final double tsfc,
            final double lapseRateFactor,
            final FuelType fuelType) {

        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

        final EngineModelId id = EngineModelId.of(name, makerId);

        if (repository.findById(id).isPresent())
            throw new IllegalStateException(
                    "An engine model with name '" + name
                            + "' and maker '" + makerId + "' already exists.");

        final EngineType type = EngineType.fromString(engineType);

        final EngineModel model = new EngineModel(
                id, type,
                Thrust.of(thrustAtStandstill), Thrust.of(thrustAtCruiseSpeed), Speed.of(cruiseSpeedMach),
                TSFC.of(tsfc), Coefficient.of(lapseRateFactor), fuelType);

        return repository.save(model);
    }

    /**
     * Returns all engine models — used by the UI to list available engines
     * and by US055 to select certified engines for an aircraft model.
     */
    public Iterable<EngineModel> allEngineModels() {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return repository.findAll();
    }

    /**
     * Returns all available fuel types — used by the UI to present selection list.
     */
    public FuelType[] allFuelTypes() {
        return FuelType.values();
    }
}