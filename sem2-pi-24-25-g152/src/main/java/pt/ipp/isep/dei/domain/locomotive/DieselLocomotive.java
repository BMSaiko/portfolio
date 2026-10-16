package pt.ipp.isep.dei.domain.locomotive;

import java.util.Date;

public class DieselLocomotive extends Locomotive {
    private double fuelCapacity;
    private double horsePower;

    public DieselLocomotive(String id, String name, double power, double speed, double price,
                            Date availableFrom, Date availableUntil, double maintenanceCost,
                            double fuelCapacity, double horsePower) {
        super(id, name, power, speed, price, availableFrom, availableUntil, maintenanceCost);
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public double getFuelCost() {
        return 0.0;
    }

    @Override
    public double getEfficiency() {
        return 0.0;
    }

    @Override
    public int getMaxCarriages() {
        return 0;
    }

    @Override
    public boolean isCompatibleWith(Carriage carriage) {
        return false;
    }

    @Override
    public double getEnvironmentalImpact() {
        return 0.0;
    }

    public double getFuelCapacity() {
        return 0.0;
    }

    public double getHorsePower() {
        return 0.0;
    }
}
