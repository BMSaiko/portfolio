package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;
import pt.ipp.isep.dei.domain.map.Station;

import java.util.Date;
import java.util.List;

public interface IBuildingUpgradeRepository {
    List<BuildingUpgrade> getAvailableUpgrades(Station station, Date currentDate);
    BuildingUpgrade getUpgradeById(String upgradeId);
    List<BuildingUpgrade> getAllUpgrades();
    List<BuildingUpgrade> getUpgradesByType(String buildingType);
}
