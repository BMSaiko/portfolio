package eapli.aisafe.enginemanagement.domain;

import eapli.aisafe.generic.domain.Thrust;
import eapli.aisafe.generic.domain.Speed;
import eapli.aisafe.generic.domain.TSFC;
import eapli.aisafe.generic.domain.Coefficient;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ENGINE_MODEL",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_ENGINE_MODEL_NAME_MAKER",
                columnNames = {"engine_name", "engine_maker_id"}))
public class EngineModel {

    @EmbeddedId
    private EngineModelId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "engine_type", nullable = false)
    private EngineType engineType;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "thrust_standstill", nullable = false))
    private Thrust thrustAtStandstill;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "thrust_cruise", nullable = false))
    private Thrust thrustAtCruiseSpeed;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "cruise_speed_mach", nullable = false))
    private Speed cruiseSpeedMach;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "tsfc", nullable = false))
    private TSFC tsfc;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "lapse_rate_factor", nullable = false))
    private Coefficient lapseRateFactor;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    protected EngineModel() {}

    public EngineModel(final EngineModelId id,
                       final EngineType engineType,
                       final Thrust thrustAtStandstill,
                       final Thrust thrustAtCruiseSpeed,
                       final Speed cruiseSpeedMach,
                       final TSFC tsfc,
                       final Coefficient lapseRateFactor,
                       final FuelType fuelType) {
        if (id == null)
            throw new IllegalArgumentException("Engine model ID must not be null.");
        if (engineType == null)
            throw new IllegalArgumentException("Engine type must not be null.");
        if (thrustAtStandstill == null)
            throw new IllegalArgumentException("Thrust at standstill must not be null.");
        if (thrustAtCruiseSpeed == null)
            throw new IllegalArgumentException("Thrust at cruise speed must not be null.");
        if (thrustAtCruiseSpeed.value() >= thrustAtStandstill.value())
            throw new IllegalArgumentException("Thrust at cruise speed must be less than thrust at standstill.");
        if (cruiseSpeedMach == null)
            throw new IllegalArgumentException("Cruise speed must not be null.");
        if (tsfc == null)
            throw new IllegalArgumentException("TSFC must not be null.");
        if (lapseRateFactor == null)
            throw new IllegalArgumentException("Lapse-rate factor must not be null.");
        if (fuelType == null)
            throw new IllegalArgumentException("Fuel type must not be null.");

        this.id = id;
        this.engineType = engineType;
        this.thrustAtStandstill = thrustAtStandstill;
        this.thrustAtCruiseSpeed = thrustAtCruiseSpeed;
        this.cruiseSpeedMach = cruiseSpeedMach;
        this.tsfc = tsfc;
        this.lapseRateFactor = lapseRateFactor;
        this.fuelType = fuelType;
    }

    public EngineModelId identity() { return id; }

    public boolean sameAs(final Object other) {
        if (!(other instanceof EngineModel)) return false;
        return this.id.equals(((EngineModel) other).id);
    }

    public String name()                { return id.name(); }
    public String makerId()             { return id.makerId(); }
    public EngineType engineType()      { return engineType; }
    public Thrust thrustAtStandstill()  { return thrustAtStandstill; }
    public Thrust thrustAtCruiseSpeed() { return thrustAtCruiseSpeed; }
    public Speed cruiseSpeedMach()      { return cruiseSpeedMach; }
    public TSFC tsfc()                  { return tsfc; }
    public Coefficient lapseRateFactor(){ return lapseRateFactor; }
    public FuelType fuelType()          { return fuelType; }

    public double thrustAtAltitude(final double airDensity) {
        final double rho0 = 1.225;
        return thrustAtStandstill.value() * Math.pow(airDensity / rho0, lapseRateFactor.value());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EngineModel)) return false;
        return id.equals(((EngineModel) o).id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("[%s] Type: %s | Thrust(SL): %s | Thrust(cruise): %s"
                        + " | CruiseMach: %s | TSFC: %s | LapseFactor: %s | Fuel: %s",
                id, engineType, thrustAtStandstill, thrustAtCruiseSpeed,
                cruiseSpeedMach, tsfc, lapseRateFactor, fuelType.label());
    }
}
