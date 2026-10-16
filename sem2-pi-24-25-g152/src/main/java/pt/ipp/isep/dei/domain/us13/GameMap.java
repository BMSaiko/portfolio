package pt.ipp.isep.dei.domain.us13;

import java.util.ArrayList;
import java.util.Collections;
import pt.ipp.isep.dei.domain.scenario.*; // Adjust the package path if necessary
import java.util.List;

/**
 * Represents a map in the railway system.
 */
public class GameMap {
    private final String name;
    private final int width;
    private final int height;
    private final List<Industry> industries;
    
    /**
     * Creates a new map.
     * 
     * @param name the name of the map
     * @param width the width of the map
     * @param height the height of the map
     */
    public GameMap(String name, int width, int height) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Map name cannot be null or empty");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive integers");
        }
        
        this.name = name;
        this.width = width;
        this.height = height;
        this.industries = new ArrayList<>();
    }
    
    /**
     * Gets the name of the map.
     * 
     * @return the map name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the width of the map.
     * 
     * @return the map width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the map.
     * 
     * @return the map height
     */
    public int getHeight() {
        return height;
    }
}