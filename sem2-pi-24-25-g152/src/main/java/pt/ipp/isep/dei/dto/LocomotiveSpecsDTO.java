package pt.ipp.isep.dei.dto;

import java.util.Date;
import java.util.Map;

public class LocomotiveSpecsDTO {
    private String id;
    private String name;
    private double power;
    private double speed;
    private double price;
    private Date availableFrom;
    private Date availableUntil;
    private double maintenanceCost;
    private Map<String, Object> additionalSpecs;

    public LocomotiveSpecsDTO(String id, String name, double power, double speed, double price,
                              Date availableFrom, Date availableUntil, double maintenanceCost,
                              Map<String, Object> additionalSpecs) {
    }

}
