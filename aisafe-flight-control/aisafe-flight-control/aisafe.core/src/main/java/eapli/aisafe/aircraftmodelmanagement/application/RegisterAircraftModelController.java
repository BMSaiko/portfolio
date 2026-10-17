package eapli.aisafe.aircraftmodelmanagement.application;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegisterAircraftModelController {
    private final AuthService authService;
    private final AircraftModelRepository aircraftModelRepository;
    private final EngineModelRepository engineModelRepository;

    public RegisterAircraftModelController(final AuthService authService,
                                           final AircraftModelRepository aircraftModelRepository,
                                           final EngineModelRepository engineModelRepository) {
        this.authService = authService;
        this.aircraftModelRepository = aircraftModelRepository;
        this.engineModelRepository = engineModelRepository;
    }

    public AircraftModel registerAircraftModel(
            final String modelId, final String makerName, final String aircraftType,
            final double emptyWeight, final double mtow, final double mzfw,
            final double maxFuelCapacity, final double serviceCeiling,
            final double cruiseSpeedMach, final double wingArea,
            final double wingSpan, final double aspectRatio,
            final double oswaldFactor, final double dragCoefficient,
            final double liftCoefficient, final int numberOfEngines,
            final int maxPassengerCapacity, final double maxRange,
            final List<EngineModelId> engineIds) {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        final AircraftModelId id = AircraftModelId.of(modelId, makerName);
        if (aircraftModelRepository.findById(id).isPresent())
            throw new IllegalStateException("An aircraft model with ID '" + modelId + "' and maker '" + makerName + "' already exists.");
        if (engineIds == null || engineIds.isEmpty())
            throw new IllegalArgumentException("At least one certified engine model must be provided.");
        final Set<EngineModel> engines = resolveEngines(engineIds);
        final AircraftType type = AircraftType.fromString(aircraftType);
        final AircraftModel model = new AircraftModel(
                id, type,
                Weight.of(emptyWeight), Weight.of(mtow), Weight.of(mzfw), Weight.of(maxFuelCapacity),
                Altitude.of(serviceCeiling), Speed.of(cruiseSpeedMach),
                Area.of(wingArea), wingSpan, aspectRatio, Coefficient.of(oswaldFactor),
                Coefficient.of(dragCoefficient), Coefficient.of(liftCoefficient),
                numberOfEngines, maxPassengerCapacity, maxRange, engines);
        return aircraftModelRepository.save(model);
    }

    public Iterable<AircraftModel> allAircraftModels() {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return aircraftModelRepository.findAll();
    }

    public Iterable<EngineModel> allEngineModels() {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        return engineModelRepository.findAll();
    }

    private Set<EngineModel> resolveEngines(final List<EngineModelId> engineIds) {
        final Set<EngineModel> engines = new HashSet<>();
        for (final EngineModelId engineId : engineIds) {
            final EngineModel engine = engineModelRepository.findById(engineId)
                    .orElseThrow(() -> new IllegalStateException("Engine model not found: " + engineId));
            engines.add(engine);
        }
        return engines;
    }
}
