package eapli.aisafe.enginemanagement.domain;

public enum EngineType {
    TURBOPROP,
    TURBOFAN,
    TURBOJET,
    RAMJET,
    ELECTRIC_PROPELLER;

    public static EngineType fromString(final String value) {
        for (EngineType type : values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown engine type: '" + value + "'.");
    }
}
