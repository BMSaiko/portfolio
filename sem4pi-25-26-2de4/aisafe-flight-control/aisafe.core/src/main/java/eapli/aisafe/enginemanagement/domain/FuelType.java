package eapli.aisafe.enginemanagement.domain;

/**
 * Aviation fuel types with their physical constants.
 *
 * <p>Physical constants per requirements section 3.3:
 * JET A-1: density = 0.804 kg/L at 15°C, specific energy = 42.80 MJ/kg
 *
 * <p>Pre-loaded list presented to the operator during engine model registration.
 */
public enum FuelType {

    JET_A1("JET A-1", 0.804, 42.80),
    JET_A("JET A",    0.800, 42.80),
    AVGAS("AVGAS 100LL", 0.720, 43.71);

    private final String label;

    /** Density at 15°C (kg/L). */
    private final double density;

    /** Specific energy (MJ/kg). */
    private final double specificEnergy;

    FuelType(final String label, final double density, final double specificEnergy) {
        this.label         = label;
        this.density       = density;
        this.specificEnergy = specificEnergy;
    }

    public String label()           { return label; }
    public double density()         { return density; }
    public double specificEnergy()  { return specificEnergy; }

    @Override
    public String toString() {
        return String.format("%s (density: %.3f kg/L, energy: %.2f MJ/kg)",
                label, density, specificEnergy);
    }
}