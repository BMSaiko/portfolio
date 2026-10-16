package pt.ipp.isep.dei.domain.locomotive;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a train with a locomotive and carriages.
 */
public class Train {
    private String id;
    private String name;
    private Locomotive locomotive;
    private List<Carriage> carriages;
    private Route route;

    /**
     * Constructor for a train.
     * 
     * @param id the train ID
     * @param name the train name
     * @param locomotive the locomotive powering the train
     */
    public Train(String id, String name, Locomotive locomotive) {
        this.id = id;
        this.name = name;
        this.locomotive = locomotive;
        this.carriages = new ArrayList<>();
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
     * Gets the locomotive.
     * 
     * @return the locomotive
     */
    public Locomotive getLocomotive() {
        return locomotive;
    }

    /**
     * Gets the train route.
     * 
     * @return the train route, or null if not assigned
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Sets the route for the train.
     * 
     * @param route the route to assign
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    /**
     * Gets the list of carriages.
     * 
     * @return the list of carriages
     */
    public List<Carriage> getCarriages() {
        return new ArrayList<>(carriages);
    }

    /**
     * Adds a carriage to the train.
     * 
     * @param carriage the carriage to add
     * @return true if the carriage was added successfully, false otherwise
     */
    public boolean addCarriage(Carriage carriage) {
        if (carriage == null) {
            return false;
        }
        return carriages.add(carriage);
    }

    /**
     * Gets the list of cargo carried by this train.
     * 
     * @return the list of cargo
     */
    public List<Cargo> getCargo() {
        List<Cargo> result = new ArrayList<>();
        for (Carriage carriage : carriages) {
            // Additional cargo handling would go here when Carriage class is updated
        }
        return result;
    }

    /**
     * Checks if the train has any cargo.
     * 
     * @return true if the train has cargo, false otherwise
     */
    public boolean hasCargo() {
        return !getCargo().isEmpty();
    }
}
