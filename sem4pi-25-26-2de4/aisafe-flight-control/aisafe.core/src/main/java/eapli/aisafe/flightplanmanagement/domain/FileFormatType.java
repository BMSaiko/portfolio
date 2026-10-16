package eapli.aisafe.flightplanmanagement.domain;

/**
 * Enum representing the recognized file formats for flight plan import.
 *
 * <p>Used by {@link FileFormatValidator} to classify input files.</p>
 */
public enum FileFormatType {

    /**
     * A recognized flight plan DSL file.
     */
    FLIGHT_DSL,

    /**
     * A file with an unrecognized extension.
     */
    UNKNOWN,

    /**
     * A file that is empty, corrupted, or otherwise not processable.
     */
    INVALID
}
