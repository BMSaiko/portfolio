package pt.ipp.isep.dei.domain.building;

import java.util.Date;

public class TelephoneBuilding extends BuildingUpgrade {
    public TelephoneBuilding(String id, String name, double cost, Date availableFrom, String description) {
        super(id, name, cost, availableFrom, description);
    }

    @Override
    public String getType() {
        return "TELEPHONE";
    }

    @Override
    public double getMaintenanceCost() {
        // TODO: Implementation
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
