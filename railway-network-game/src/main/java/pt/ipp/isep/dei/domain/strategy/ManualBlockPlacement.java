package pt.ipp.isep.dei.domain.strategy;

import pt.ipp.isep.dei.domain.map.HouseBlock;
import pt.ipp.isep.dei.domain.map.Location;
import java.util.List;

/**
 * Manual block placement using provided coordinates
 */
public class ManualBlockPlacement implements BlockPlacementStrategy {
    private List<Location> blockLocations;

    public ManualBlockPlacement() {
    }

    public ManualBlockPlacement(List<Location> blockLocations) {
        this.blockLocations = blockLocations;
    }

    @Override
    public List<HouseBlock> placeBlocks(Location cityLocation, int count, int mapWidth, int mapHeight) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Getters and setters
    public List<Location> getBlockLocations() { return blockLocations; }
    public void setBlockLocations(List<Location> blockLocations) { this.blockLocations = blockLocations; }
}
