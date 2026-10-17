package eapli.aisafe.aircraftmodelmanagement.domain;

/**
 * Classification of an aircraft model by its primary purpose.
 */
public enum AircraftType {
    PASSENGER,
    CARGO,
    MIXED;

    public static AircraftType fromString(final String value) {
        for (AircraftType type : values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown aircraft type: '" + value + "'.");
    }
}
