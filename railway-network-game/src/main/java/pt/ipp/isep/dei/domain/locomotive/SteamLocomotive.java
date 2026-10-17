package pt.ipp.isep.dei.domain.locomotive;

import java.util.Date;

public class SteamLocomotive extends Locomotive {
    private double coalConsumption;
    private double waterCapacity;

    public SteamLocomotive(String id, String name, double power, double speed, double price,
                           Date availableFrom, Date availableUntil, double maintenanceCost,
                           double coalConsumption, double waterCapacity) {
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

    public double getCoalConsumption() {
        return 0.0;
    }

    public double getWaterCapacity() {
        return 0.0;
    }
}
