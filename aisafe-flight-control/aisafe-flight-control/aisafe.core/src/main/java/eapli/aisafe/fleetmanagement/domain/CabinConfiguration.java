package eapli.aisafe.fleetmanagement.domain;

import eapli.framework.domain.model.ValueObject;
import jakarta.persistence.Embeddable;

@Embeddable
public class CabinConfiguration implements ValueObject {

    private int firstClassSeats;
    private int businessSeats;
    private int economySeats;

    protected CabinConfiguration() {
        // For ORM only
    }

    public CabinConfiguration(int firstClassSeats, int businessSeats, int economySeats) {
        if (firstClassSeats < 0 || businessSeats < 0 || economySeats < 0) {
            throw new IllegalArgumentException("Seat numbers cannot be negative.");
        }
        this.firstClassSeats = firstClassSeats;
        this.businessSeats = businessSeats;
        this.economySeats = economySeats;
    }

    public int totalSeats() {
        return this.firstClassSeats + this.businessSeats + this.economySeats;
    }
}