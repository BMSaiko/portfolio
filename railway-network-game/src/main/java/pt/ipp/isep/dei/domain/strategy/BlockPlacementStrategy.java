package pt.ipp.isep.dei.domain.strategy;

import java.util.List;
import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.domain.map.HouseBlock;


public interface BlockPlacementStrategy {
    List<HouseBlock> placeBlocks(Location cityLocation, int count, int mapWidth, int mapHeight);
}