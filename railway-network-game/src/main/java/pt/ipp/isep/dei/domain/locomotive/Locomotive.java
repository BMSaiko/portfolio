package pt.ipp.isep.dei.domain.locomotive;

import java.util.Date;

public abstract class Locomotive {
    protected String id;
    protected String name;
    protected double power;
    protected double speed;
    protected double price;
    protected Date availableFrom;
    protected Date availableUntil;
    protected double maintenanceCost;

    public Locomotive(String id, String name, double power, double speed, double price,
                      Date availableFrom, Date availableUntil, double maintenanceCost) {
    }

    public abstract String getType();
    public abstract double getFuelCost();
    public abstract double getEfficiency();
    public abstract int getMaxCarriages();
    public abstract boolean isCompatibleWith(Carriage carriage);
    public abstract double getEnvironmentalImpact();

    public double getPrice() {
        return 0.0;
    }

    public Date getAvailableFrom() {
        return null;
    }

    public Date getAvailableUntil() {
        return null;
    }

    public boolean isAvailableAt(Date date) {
        return false;
    }

    public double getMaintenanceCost() {
        return 0.0;
    }

    public double calculateOperatingCost(double distance) {
        return 0.0;
    }
}
