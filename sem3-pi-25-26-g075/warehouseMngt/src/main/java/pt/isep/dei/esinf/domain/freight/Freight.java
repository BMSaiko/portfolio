package pt.isep.dei.esinf.domain.freight;

import pt.isep.dei.esinf.domain.railway.Facility;
import java.time.LocalDate;

/**
 * Represents a freight that needs to be transported from origin to destination
 */
public class Freight {
    private int freightId;
    private LocalDate freightDate;
    private Facility origin;
    private Facility destination;
    private FreightStatus status;
    private Integer assignedTrainId;

    public Freight(int freightId, LocalDate freightDate, Facility origin, Facility destination) {
        this.freightId = freightId;
        this.freightDate = freightDate;
        this.origin = origin;
        this.destination = destination;
        this.status = FreightStatus.PENDING;
    }

    // Getters
    public int getFreightId() {
        return freightId;
    }

    public LocalDate getFreightDate() {
        return freightDate;
    }

    public Facility getOrigin() {
        return origin;
    }

    public Facility getDestination() {
        return destination;
    }

    public FreightStatus getStatus() {
        return status;
    }

    public Integer getAssignedTrainId() {
        return assignedTrainId;
    }

    // Setters
    public void setStatus(FreightStatus status) {
        this.status = status;
    }

    public void setAssignedTrainId(Integer trainId) {
        this.assignedTrainId = trainId;
    }

    @Override
    public String toString() {
        return String.format("Freight #%d [%s → %s] on %s",
            freightId, origin.getName(), destination.getName(), freightDate);
    }
}

