package pt.ipp.isep.dei.domain.factory;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;

import java.util.Date;

public class CafeBuildingFactory implements BuildingUpgradeFactory {
    @Override
    public BuildingUpgrade createBuilding(String id, String name, double cost, Date availableFrom, String description) {
        // TODO: Implementation - decide between Small and Large cafe based on parameters
        return null;
    }

    @Override
    public String getSupportedType() {
        return "CAFE";
    }
}
