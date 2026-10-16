package pt.ipp.isep.dei.dto;

import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.domain.map.Station;

import java.util.List;

public class StationDTO {
    private String id;
    private String name;
    private String type;
    private List<InstalledUpgradeDTO> installedUpgrades;
    private int availableUpgradeSlots;
    public Location location;
    public double storageCapacity;
    public int currentCargoCount;

    public StationDTO(Station station) {
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<InstalledUpgradeDTO> getInstalledUpgrades() {
        return installedUpgrades;
    }

    public void setInstalledUpgrades(List<InstalledUpgradeDTO> installedUpgrades) {
        this.installedUpgrades = installedUpgrades;
    }

    public int getAvailableUpgradeSlots() {
        return availableUpgradeSlots;
    }

    public void setAvailableUpgradeSlots(int availableUpgradeSlots) {
        this.availableUpgradeSlots = availableUpgradeSlots;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(double storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public int getCurrentCargoCount() {
        return currentCargoCount;
    }

    public void setCurrentCargoCount(int currentCargoCount) {
        this.currentCargoCount = currentCargoCount;
    }
}
