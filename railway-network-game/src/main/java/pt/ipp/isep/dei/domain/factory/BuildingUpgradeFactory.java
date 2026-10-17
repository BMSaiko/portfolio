package pt.ipp.isep.dei.domain.factory;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;

import java.util.Date;

public interface BuildingUpgradeFactory {
    BuildingUpgrade createBuilding(String id, String name, double cost, Date availableFrom, String description);
    String getSupportedType();
}
