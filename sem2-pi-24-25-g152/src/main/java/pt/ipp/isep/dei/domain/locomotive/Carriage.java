package pt.ipp.isep.dei.domain.locomotive;

import java.util.List;

public abstract class Carriage {
    protected String id;
    protected double weight;
    protected double capacity;
    protected double purchasePrice;

    public Carriage(String id, double weight, double capacity, double purchasePrice) {
    }

    public abstract String getCargoType();
    public abstract double getLoadingTime();
    public abstract List<String> getSpecialRequirements();
    public abstract double getRevenuePotential();

    public double getWeight() {
        return 0.0;
    }

    public double getCapacity() {
        return 0.0;
    }

    public double getPurchasePrice() {
        return 0.0;
    }
}
