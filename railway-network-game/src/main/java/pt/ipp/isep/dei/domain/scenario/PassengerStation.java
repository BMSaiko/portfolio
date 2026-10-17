package pt.ipp.isep.dei.domain.scenario;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;
import pt.ipp.isep.dei.domain.map.Map;

import java.util.List;

public class PassengerStation extends Station {
    public PassengerStation(int x, int y, String name, Map map) {
        super(x, y, name, map);
    }

    @Override
    public double getCost() {
        return 0.0;
    }

    @Override
    public int getRadius() {
        return 0;
    }

    @Override
    public String getType() {
        return "PASSENGER";
    }

    public int getPassengerCapacity() {
        return 0;
    }

    @Override
    public boolean validateUpgrade(BuildingUpgrade buildingUpgrade) {
        return false;
    }

    @Override
    public List<String> getCompatibleBuildingTypes() {
        return null;
    }

    @Override
    public int getMaxBuildingsAllowed() {
        return 0;
    }
}

