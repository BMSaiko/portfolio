package eapli.aisafe.fleetmanagement.dto;

import java.io.Serializable;

/**
 * DTO for decommissioning an aircraft via TCP.
 * US071 — Decommission an aircraft.
 */
public class AircraftDecommissionDTO implements Serializable {
    public final String registration;

    public AircraftDecommissionDTO(final String registration) {
        this.registration = registration;
    }
}
