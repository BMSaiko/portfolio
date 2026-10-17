package eapli.aisafe.fleetmanagement.dto;

import java.io.Serializable;

public class AircraftRegistrationDTO implements Serializable {
    public String registration;
    public String modelId;
    public String makerName;
    public String country;
    public int firstClassSeats;
    public int businessSeats;
    public int economySeats;
    public String status;
    public final int manufacturingYear;

    public AircraftRegistrationDTO(String registration, String modelId, String makerName, String country, int firstClassSeats, int businessSeats, int economySeats, String status, int manufacturingYear) {
        this.registration = registration;
        this.modelId = modelId;
        this.makerName = makerName;
        this.country = country;
        this.firstClassSeats = firstClassSeats;
        this.businessSeats = businessSeats;
        this.economySeats = economySeats;
        this.status = status;
        this.manufacturingYear = manufacturingYear;
    }
}