package pt.ipp.isep.dei.domain.us13;

/**
 * Enum representing the different types of stations in the railway network.
 */
public enum StationType {
    DEPOT("D"),
    STATION("S"),
    TERMINAL("T");
    
    private final String prefix;
    
    StationType(String prefix) {
        this.prefix = prefix;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * Determines the station type based on the prefix in the station name.
     * 
     * @param stationName the name of the station
     * @return the type of the station
     */
    public static StationType fromName(String stationName) {
        if (stationName.startsWith(DEPOT.prefix)) {
            return DEPOT;
        } else if (stationName.startsWith(STATION.prefix)) {
            return STATION;
        } else if (stationName.startsWith(TERMINAL.prefix)) {
            return TERMINAL;
        }
        throw new IllegalArgumentException("Invalid station name prefix: " + stationName);
    }
}