package pt.ipp.isep.dei.domain.locomotive;


import pt.ipp.isep.dei.domain.map.Station;

public class Cargo {
    private int id;
    private String type;
    private Station origin;
    private Station destination;
    private double quantity;
    private float weight;

    public Cargo(String type, Station origin, Station destination, double quantity, int id, float weight) {
        this.type = type;
        this.origin = origin;
        this.destination = destination;
        this.quantity = quantity;
        this.id = id;
        this.weight = weight;
    }

    public Object getType() {
        return type;
    }

    public Station getOrigin() {
        return origin;
    }

    public Station getDestination() {
        return destination;
    }

    public double getQuantity() {
        return quantity;
    }

    public boolean validateData() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOrigin(Station origin) {
        this.origin = origin;
    }

    public void setDestination(Station destination) {
        this.destination = destination;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
