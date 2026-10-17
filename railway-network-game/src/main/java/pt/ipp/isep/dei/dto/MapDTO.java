package pt.ipp.isep.dei.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Map information.
 */
public class MapDTO {
    public Long id;
    private String name;
    private int width;
    private int height;
    private List<String> industries;

    /**
     * Creates a new MapDTO.
     *
     * @param name the name of the map
     * @param width the width of the map
     * @param height the height of the map
     */
    public MapDTO(long id, String name, int width, int height) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.industries = new ArrayList<>();
    }

    /**
     * Gets the id of the map.
     *
     * @return the map id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id of the map.
     *
     */
    public void setId(long id) {
        this.id = id;
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

    /**
     * Gets the industries in the map.
     *
     * @return the map industries
     */
    public List<String> getIndustries() {
        return new ArrayList<>(industries);
    }

    /**
     * Adds an industry to the map.
     *
     * @param industry the industry to add
     */
    public void addIndustry(String industry) {
        this.industries.add(industry);
    }
}