package pt.ipp.isep.dei.dto;

/**
 * Data Transfer Object for Cargo information.
 */
public class CargoDTO {
    private String type;
    private int quantity;
    private String source;
    private String destination;

    /**
     * Constructor for CargoDTO.
     * 
     * @param type the cargo type
     * @param quantity the cargo quantity
     * @param source the source station or industry
     * @param destination the destination station or industry
     */
    public CargoDTO(String type, int quantity, String source, String destination) {
        this.type = type;
        this.quantity = quantity;
        this.source = source;
        this.destination = destination;
    }

    /**
     * Gets the cargo type.
     * 
     * @return the cargo type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the cargo quantity.
     * 
     * @return the cargo quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the cargo source.
     * 
     * @return the cargo source
     */
    public String getSource() {
        return source;
    }

    /**
     * Gets the cargo destination.
     * 
     * @return the cargo destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Returns a string representation of the cargo.
     * 
     * @return a string representation of the cargo
     */
    @Override
    public String toString() {
        return quantity + " units of " + type + " from " + source + " to " + destination;
    }
}
