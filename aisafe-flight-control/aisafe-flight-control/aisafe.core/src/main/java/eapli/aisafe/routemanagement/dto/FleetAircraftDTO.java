package eapli.aisafe.routemanagement.dto;

import java.io.Serializable;

/**
 * DTO for carrying aircraft data in fleet listings over TCP.
 */
public class FleetAircraftDTO implements Serializable {
    public final String registration;
    public final String modelId;
    public final String makerName;
    public final String type;
    public final int totalSeats;
    public final String status;
    public final int manufacturingYear;

    public FleetAircraftDTO(String registration, String modelId, String makerName,
                            String type, int totalSeats, String status, int manufacturingYear) {
        this.registration = registration;
        this.modelId = modelId;
        this.makerName = makerName;
        this.type = type;
        this.totalSeats = totalSeats;
        this.status = status;
        this.manufacturingYear = manufacturingYear;
    }
}
