package eapli.aisafe.aircraftmodelmanagement.domain;

import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.generic.domain.*;

import jakarta.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "AIRCRAFT_MODEL",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_AIRCRAFT_MODEL_ID_MAKER",
                columnNames = {"model_id", "maker_name"}))
public class AircraftModel {

    @EmbeddedId
    private AircraftModelId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "aircraft_type", nullable = false)
    private AircraftType aircraftType;

    @Embedded @AttributeOverride(name = "value", column = @Column(name = "empty_weight", nullable = false)) private Weight emptyWeight;
    @Embedded @AttributeOverride(name = "value", column = @Column(name = "mtow", nullable = false)) private Weight mtow;
    @Embedded @AttributeOverride(name = "value", column = @Column(name = "mzfw", nullable = false)) private Weight mzfw;
    @Embedded @AttributeOverride(name = "value", column = @Column(name = "max_fuel_capacity", nullable = false)) private Weight maxFuelCapacity;
    @Embedded @AttributeOverride(name = "value", column = @Column(name = "service_ceiling", nullable = false)) private Altitude serviceCeiling;
    @Embedded @AttributeOverride(name = "value", column = @Column(name = "cruise_speed_mach", nullable = false)) private Speed cruiseSpeedMach;
    @Embedded @AttributeOverride(name = "value", column = @Column(name = "wing_area", nullable = false)) private Area wingArea;

    @Column(name = "wing_span", nullable = false)
    private double wingSpan;

    @Column(name = "aspect_ratio", nullable = false)
    private double aspectRatio;

    @Embedded @AttributeOverride(name = "value", column = @Column(name = "oswald_factor", nullable = false)) private Coefficient oswaldFactor;
    @Embedded @AttributeOverride(name = "value", column = @Column(name = "drag_coefficient", nullable = false)) private Coefficient dragCoefficient;
    @Embedded @AttributeOverride(name = "value", column = @Column(name = "lift_coefficient", nullable = false)) private Coefficient liftCoefficient;

    @Column(name = "number_of_engines", nullable = false)
    private int numberOfEngines;

    @Column(name = "max_passenger_capacity", nullable = false)
    private int maxPassengerCapacity;

    @Column(name = "max_range", nullable = false)
    private double maxRange;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "AIRCRAFT_MODEL_ENGINES",
            joinColumns = {
                    @JoinColumn(name = "model_id", referencedColumnName = "model_id"),
                    @JoinColumn(name = "maker_name", referencedColumnName = "maker_name")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "engine_name", referencedColumnName = "engine_name"),
                    @JoinColumn(name = "engine_maker_id", referencedColumnName = "engine_maker_id")
            }
    )
    private Set<EngineModel> certifiedEngines = new HashSet<>();

    protected AircraftModel() {}

    public AircraftModel(final AircraftModelId id,
                         final AircraftType aircraftType,
                         final Weight emptyWeight,
                         final Weight mtow,
                         final Weight mzfw,
                         final Weight maxFuelCapacity,
                         final Altitude serviceCeiling,
                         final Speed cruiseSpeedMach,
                         final Area wingArea,
                         final double wingSpan,
                         final double aspectRatio,
                         final Coefficient oswaldFactor,
                         final Coefficient dragCoefficient,
                         final Coefficient liftCoefficient,
                         final int numberOfEngines,
                         final int maxPassengerCapacity,
                         final double maxRange,
                         final Set<EngineModel> certifiedEngines) {

        if (id == null) throw new IllegalArgumentException("Aircraft model ID must not be null.");
        if (aircraftType == null) throw new IllegalArgumentException("Aircraft type must not be null.");
        if (emptyWeight == null) throw new IllegalArgumentException("Empty weight must not be null.");
        if (mtow == null) throw new IllegalArgumentException("MTOW must not be null.");
        if (mzfw == null) throw new IllegalArgumentException("MZFW must not be null.");
        if (maxFuelCapacity == null) throw new IllegalArgumentException("Max fuel capacity must not be null.");
        if (serviceCeiling == null) throw new IllegalArgumentException("Service ceiling must not be null.");
        if (cruiseSpeedMach == null) throw new IllegalArgumentException("Cruise speed must not be null.");
        if (wingArea == null) throw new IllegalArgumentException("Wing area must not be null.");
        if (wingSpan <= 0) throw new IllegalArgumentException("Wing span must be positive.");
        if (aspectRatio <= 0) throw new IllegalArgumentException("Aspect ratio must be positive.");
        if (oswaldFactor == null) throw new IllegalArgumentException("Oswald factor must not be null.");
        if (oswaldFactor.value() > 1) throw new IllegalArgumentException("Oswald factor must not exceed 1.");
        if (dragCoefficient == null) throw new IllegalArgumentException("Drag coefficient must not be null.");
        if (liftCoefficient == null) throw new IllegalArgumentException("Lift coefficient must not be null.");
        if (mtow.value() <= emptyWeight.value()) throw new IllegalArgumentException("MTOW must be greater than empty weight.");
        if (mzfw.value() > mtow.value()) throw new IllegalArgumentException("MZFW must not exceed MTOW.");
        if (numberOfEngines < 1) throw new IllegalArgumentException("Number of engines must be at least 1.");
        if (maxPassengerCapacity < 0) throw new IllegalArgumentException("Max passenger capacity must not be negative.");
        if (maxRange <= 0) throw new IllegalArgumentException("Max range must be positive.");
        if (certifiedEngines == null || certifiedEngines.isEmpty())
            throw new IllegalArgumentException("At least one certified engine model must be provided.");
        validateEngineTypeHomogeneity(certifiedEngines);
        this.id = id;
        this.aircraftType = aircraftType;
        this.emptyWeight = emptyWeight;
        this.mtow = mtow;
        this.mzfw = mzfw;
        this.maxFuelCapacity = maxFuelCapacity;
        this.serviceCeiling = serviceCeiling;
        this.cruiseSpeedMach = cruiseSpeedMach;
        this.wingArea = wingArea;
        this.wingSpan = wingSpan;
        this.aspectRatio = aspectRatio;
        this.oswaldFactor = oswaldFactor;
        this.dragCoefficient = dragCoefficient;
        this.liftCoefficient = liftCoefficient;
        this.numberOfEngines = numberOfEngines;
        this.maxPassengerCapacity = maxPassengerCapacity;
        this.maxRange = maxRange;
        this.certifiedEngines = new HashSet<>(certifiedEngines);
    }

    private static void validateEngineTypeHomogeneity(final Set<EngineModel> engines) {
        final EngineType expected = engines.iterator().next().engineType();
        for (final EngineModel engine : engines) {
            if (engine.engineType() != expected)
                throw new IllegalArgumentException("All certified engines must be the same type.");
        }
    }

    public void addCertifiedEngine(final EngineModel engine) {
        if (engine == null) throw new IllegalArgumentException("Engine model must not be null.");
        if (certifiedEngines.contains(engine))
            throw new IllegalStateException("Engine model already certified.");
        validateEngineTypeHomogeneity(engine);
        certifiedEngines.add(engine);
    }

    private void validateEngineTypeHomogeneity(final EngineModel candidate) {
        if (certifiedEngines.isEmpty()) return;
        final EngineType existing = certifiedEngines.iterator().next().engineType();
        if (candidate.engineType() != existing)
            throw new IllegalArgumentException("Engine type mismatch.");
    }

    public void removeCertifiedEngine(final EngineModel engine) {
        if (engine == null) throw new IllegalArgumentException("Engine model must not be null.");
        if (!certifiedEngines.contains(engine))
            throw new IllegalStateException("Engine model not certified.");
        if (certifiedEngines.size() == 1)
            throw new IllegalStateException("Cannot remove the last certified engine.");
        certifiedEngines.remove(engine);
    }

    public AircraftModelId identity() { return id; }
    public boolean sameAs(final Object other) { return other instanceof AircraftModel && this.id.equals(((AircraftModel) other).id); }
    public String modelId() { return id.modelId(); }
    public String makerName() { return id.makerName(); }
    public AircraftType aircraftType() { return aircraftType; }
    public Weight emptyWeight() { return emptyWeight; }
    public Weight mtow() { return mtow; }
    public Weight mzfw() { return mzfw; }
    public Weight maxFuelCapacity() { return maxFuelCapacity; }
    public Altitude serviceCeiling() { return serviceCeiling; }
    public Speed cruiseSpeedMach() { return cruiseSpeedMach; }
    public Area wingArea() { return wingArea; }
    public double wingSpan() { return wingSpan; }
    public double aspectRatio() { return aspectRatio; }
    public Coefficient oswaldFactor() { return oswaldFactor; }
    public Coefficient dragCoefficient() { return dragCoefficient; }
    public Coefficient liftCoefficient() { return liftCoefficient; }
    public int numberOfEngines() { return numberOfEngines; }
    public int maxPassengerCapacity() { return maxPassengerCapacity; }
    public double maxRange() { return maxRange; }
    public Set<EngineModel> certifiedEngines() { return Collections.unmodifiableSet(certifiedEngines); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AircraftModel)) return false;
        return id.equals(((AircraftModel) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("[%s] Type: %s | MTOW: %s | Engines: %d | Range: %.0f km",
                id, aircraftType, mtow, numberOfEngines, maxRange);
    }
}

