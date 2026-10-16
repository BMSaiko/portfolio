package pt.ipp.isep.dei.domain.building;

import java.util.Date;

public class SmallCafeBuilding extends BuildingUpgrade {
    public SmallCafeBuilding(String id, String name, double cost, Date availableFrom, String description) {
        super(id, name, cost, availableFrom, description);
    }

    @Override
    public String getType() {
        return "SMALL_CAFE";
    }

    @Override
    public double getMaintenanceCost() {
        return 0;
    }

    @Override
    public double getRevenue() {
        return 0;
    }

    @Override
    public boolean isCompatibleWith(String stationType) {
        return false;
    }

    @Override
    public boolean conflictsWith(BuildingUpgrade other) {
        return false;
    }
}
