package pt.ipp.isep.dei.domain.strategy;

import pt.ipp.isep.dei.domain.map.HouseBlock;
import pt.ipp.isep.dei.domain.map.Location;
import java.util.List;
import java.util.Random;

/**
 * Automatic block placement using normal distribution
 */
public class AutomaticBlockPlacement implements BlockPlacementStrategy {
    private Random random;

    public AutomaticBlockPlacement() {
        this.random = new Random();
    }

    @Override
    public List<HouseBlock> placeBlocks(Location cityLocation, int count, int mapWidth, int mapHeight) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Generates location using normal distribution around city center
     */
    private Location generateNormalDistributionLocation(Location center, int mapWidth, int mapHeight) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
