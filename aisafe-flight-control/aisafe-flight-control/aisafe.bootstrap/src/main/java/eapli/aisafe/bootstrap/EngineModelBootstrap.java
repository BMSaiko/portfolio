package eapli.aisafe.bootstrap;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
import eapli.aisafe.generic.domain.*;

import java.util.HashSet;
import java.util.Set;

public class EngineModelBootstrap {

    private final EngineModelRepository engineRepository;
    private final AircraftModelRepository aircraftRepository;

    public EngineModelBootstrap(final EngineModelRepository engineRepository,
                                final AircraftModelRepository aircraftRepository) {
        this.engineRepository = engineRepository;
        this.aircraftRepository = aircraftRepository;
    }

    public void load() {
        loadEngineModels();
        loadAircraftModels();
    }

    private void loadEngineModels() {
        registerEngineIfAbsent("CFM56-7B", "CFM", EngineType.TURBOFAN,
                121_000.0, 26_000.0, 0.78, 1.6e-4, 1.0, FuelType.JET_A1);

                registerEngineIfAbsent("GE 90B", "GE", EngineType.TURBOFAN,
                489_000.0, 300_000.0, 0.84, 1.6e-4, 1.0, FuelType.JET_A1);

        registerEngineIfAbsent("RR Trent 970", "RR", EngineType.TURBOFAN,
                348_310.0, 180_000.0, 0.85, 1.6e-4, 0.96, FuelType.JET_A1);

        registerEngineIfAbsent("CFM56-7B", "CFM", EngineType.TURBOFAN,
                121_000.0, 25_000.0, 0.78, 1.7e-4, 0.80, FuelType.JET_A1);
    }

    private void loadAircraftModels() {
        engineRepository.findById(EngineModelId.of("CFM56-7B", "CFM")).ifPresent(engine -> {
            final Set<EngineModel> engines = new HashSet<>();
            engines.add(engine);
            registerAircraftIfAbsent("BOEING-737", "Boeing", AircraftType.PASSENGER,
                    41_413.0, 79_016.0, 62_732.0, 26_020.0,
                    41_000.0, 0.785,
                    338.0, 35.79, 9.45, 0.85,
                    0.021, 0.5,
                    2, 189, 5_600.0, engines);
        });

        engineRepository.findById(EngineModelId.of("GE 90B", "GE")).ifPresent(engine -> {
            final Set<EngineModel> engines = new HashSet<>();
            engines.add(engine);
            registerAircraftIfAbsent("777-200ER", "Boeing", AircraftType.PASSENGER,
                    145_149.0, 347_452.0, 224_982.0, 145_538.0,
                    43_100.0, 0.84, 440.0, 60.93, 9.0, 0.87,
                    0.017, 0.5, 2, 400, 13_080.0, engines);

            registerAircraftIfAbsent("787-9", "Boeing", AircraftType.PASSENGER,
                    128_850.0, 254_011.0, 193_000.0, 126_000.0,
                    43_100.0, 0.85, 290.0, 60.12, 9.0, 0.85,
                    0.018, 0.5, 2, 290, 14_140.0, engines);
        });

        engineRepository.findById(EngineModelId.of("CFM56-7B", "CFM")).ifPresent(engine -> {
            final Set<EngineModel> engines = new HashSet<>();
            engines.add(engine);

            registerAircraftIfAbsent("737-800", "Boeing", AircraftType.PASSENGER,
                    41_413.0, 79_015.0, 62_731.0, 20_896.0,
                    41_000.0, 0.78, 189.0, 34.32, 9.4, 0.85,
                    0.020, 0.5, 2, 189, 5_436.0, engines);

            registerAircraftIfAbsent("A320-200", "Airbus", AircraftType.PASSENGER,
                    42_600.0, 78_000.0, 62_500.0, 24_210.0,
                    39_000.0, 0.78, 180.0, 34.1, 9.5, 0.82,
                    0.024, 0.5, 2, 180, 6_100.0, engines);
        });

        engineRepository.findById(EngineModelId.of("RR Trent 970", "RR")).ifPresent(engine -> {
            final Set<EngineModel> engines = new HashSet<>();
            engines.add(engine);
            registerAircraftIfAbsent("A380", "Airbus", AircraftType.PASSENGER,
                    276_800.0, 575_000.0, 361_000.0, 256_000.0,
                    43_000.0, 0.85, 845.0, 79.75, 7.53, 0.84,
                    0.020, 0.45, 4, 555, 15_200.0, engines);

            registerAircraftIfAbsent("A330-300", "Airbus", AircraftType.PASSENGER,
                    124_500.0, 242_000.0, 175_000.0, 139_090.0,
                    41_100.0, 0.82, 440.0, 60.30, 8.5, 0.83,
                    0.022, 0.5, 2, 293, 11_750.0, engines);
        });
    }

    private void registerEngineIfAbsent(
            final String name, final String makerId, final EngineType type,
            final double thrustStandstill, final double thrustCruise,
            final double cruiseMach, final double tsfc,
            final double lapseFactor, final FuelType fuelType) {

        final EngineModelId id = EngineModelId.of(name, makerId);
        if (engineRepository.findById(id).isPresent()) {
            System.out.printf("[Bootstrap] Engine model already exists, skipping: %s%n", id);
            return;
        }
        final EngineModel model = new EngineModel(id, type,
                Thrust.of(thrustStandstill), Thrust.of(thrustCruise), Speed.of(cruiseMach), TSFC.of(tsfc), Coefficient.of(lapseFactor), fuelType);
        engineRepository.save(model);
        System.out.printf("[Bootstrap] Registered engine model: %s%n", model);
    }

    private void registerAircraftIfAbsent(
            final String modelId, final String makerName, final AircraftType type,
            final double emptyWeight, final double mtow, final double mzfw,
            final double maxFuelCapacity, final double serviceCeiling,
            final double cruiseSpeedMach, final double wingArea, final double wingSpan,
            final double aspectRatio, final double oswaldFactor,
            final double dragCoefficient, final double liftCoefficient,
            final int numberOfEngines, final int maxPassengerCapacity,
            final double maxRange, final Set<EngineModel> engines) {

        final AircraftModelId id = AircraftModelId.of(modelId, makerName);
        if (aircraftRepository.findById(id).isPresent()) {
            System.out.printf("[Bootstrap] Aircraft model already exists, skipping: %s%n", id);
            return;
        }
        final AircraftModel model = new AircraftModel(id, type,
                Weight.of(emptyWeight), Weight.of(mtow), Weight.of(mzfw), Weight.of(maxFuelCapacity),
                Altitude.of(serviceCeiling), Speed.of(cruiseSpeedMach),
                Area.of(wingArea), wingSpan, aspectRatio, Coefficient.of(oswaldFactor),
                Coefficient.of(dragCoefficient), Coefficient.of(liftCoefficient),
                numberOfEngines, maxPassengerCapacity, maxRange, engines);
        aircraftRepository.save(model);
        System.out.printf("[Bootstrap] Registered aircraft model: %s%n", model);
    }
}