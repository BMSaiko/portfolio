package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.building.BuildingUpgrade;
import pt.ipp.isep.dei.dto.AvailableUpgradeDTO;
import pt.ipp.isep.dei.dto.InstalledUpgradeDTO;

import java.util.List;

public class UpgradeMapper {
    public AvailableUpgradeDTO toDTO(BuildingUpgrade upgrade, double budget, List<String> conflicts) {
        return null;
    }

    public InstalledUpgradeDTO toInstalledDTO(BuildingUpgrade upgrade) {
        return null;
    }
}
