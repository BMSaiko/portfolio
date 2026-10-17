package pt.ipp.isep.dei.domain.factory;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;

import java.util.Date;

public class HotelBuildingFactory implements BuildingUpgradeFactory {
    @Override
    public BuildingUpgrade createBuilding(String id, String name, double cost, Date availableFrom, String description) {
        return null;
    }

    @Override
    public String getSupportedType() {
        return "HOTEL";
    }
}
