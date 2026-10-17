package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;
import pt.ipp.isep.dei.domain.map.Station;

import java.util.Date;
import java.util.List;

public class BuildingUpgradeRepository implements IBuildingUpgradeRepository {
    @Override
    public List<BuildingUpgrade> getAvailableUpgrades(Station station, Date currentDate) {
        return null;
    }

    @Override
    public BuildingUpgrade getUpgradeById(String upgradeId) {
        return null;
    }

    @Override
    public List<BuildingUpgrade> getAllUpgrades() {
        return null;
    }

    @Override
    public List<BuildingUpgrade> getUpgradesByType(String buildingType) {
        return null;
    }

    private List<BuildingUpgrade> filterByCompatibility(List<BuildingUpgrade> upgrades, Station station) {
        return null;
    }

    private List<BuildingUpgrade> filterByDate(List<BuildingUpgrade> upgrades, Date currentDate) {
        return null;
    }

    private List<BuildingUpgrade> filterByConflicts(List<BuildingUpgrade> upgrades, List<BuildingUpgrade> existingUpgrades) {
        return null;
    }
}
