package pt.ipp.isep.dei.dto;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;

import java.util.Date;

public class AvailableLocomotiveDTO {
    private String id;
    private String name;
    private String type;
    private double power;
    private double speed;
    private double price;
    private double maintenanceCost;
    private double fuelCost;
    private double efficiency;
    private int maxCarriages;
    private double environmentalImpact;
    private Date availableFrom;
    private Date availableUntil;
    private boolean isAffordable;
    private double estimatedOperatingCost;

    public AvailableLocomotiveDTO(Locomotive locomotive, boolean isAffordable) {
    }

}
