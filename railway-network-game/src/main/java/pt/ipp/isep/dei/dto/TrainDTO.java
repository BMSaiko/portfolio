package pt.ipp.isep.dei.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * Data Transfer Object for Train information.
 */
public class TrainDTO {
    private String id;
    private String name;
    private String locomotiveType;
    private List<CargoDTO> cargoes;
    private RouteDTO route;
    private LocomotiveInfoDTO locomotiveInfo;
    private int carriageCount;
    private double totalWeight;
    private double maxSpeed;

    /**
     * Constructor for TrainDTO.
     * 
     * @param id the train ID
     * @param name the train name
     * @param locomotiveType the locomotive type
     */
    public TrainDTO(String id, String name, String locomotiveType) {

        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the train ID.
     * 
     * @return the train ID
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the train name.
     * 
     * @return the train name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the locomotive type.
     * 
     * @return the locomotive type
     */
    public String getLocomotiveType() {
        return locomotiveType;
    }

    /**
     * Gets the list of cargo.
     * 
     * @return the list of cargo
     */
    public List<CargoDTO> getCargoes() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Sets the list of cargo.
     * 
     * @param cargoes the list of cargo
     */
    public void setCargoes(List<CargoDTO> cargoes) {
       
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Adds a cargo to the train.
     * 
     * @param cargo the cargo to add
     */
    public void addCargo(CargoDTO cargo) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets the train route.
     * 
     * @return the train route
     */
    public RouteDTO getRoute() {
        return route;
    }

    /**
     * Sets the train route.
     * 
     * @param route the train route
     */
    public void setRoute(RouteDTO route) {
        this.route = route;
    }

    /**
     * Returns a string representation of the train.
     * 
     * @return a string representation of the train
     */
    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
