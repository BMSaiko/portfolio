package pt.ipp.isep.dei.domain.us13;

/**
 * Enum representing the different types of trains in the railway network.
 */
public enum TrainType {
    STEAM,
    DIESEL,
    ELECTRIC;
    
    /**
     * Checks if this train type can operate on a railway line.
     * Electric trains can only operate on electrified lines.
     * 
     * @param line the railway line
     * @return true if the train can operate on the line, false otherwise
     */
    public boolean canOperateOn(RailwayLine line) {
        // Electric trains can only operate on electrified lines
        if (this == ELECTRIC) {
            return line.isElectrified();
        }
        // Steam and diesel trains can operate on any line
        return true;
    }
}