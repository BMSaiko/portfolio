package pt.ipp.isep.dei.dto;

import java.util.List;

/**
 * DTO for City information
 */
public class CityDTO {
    private Long id;
    private String name;
    private LocationDTO location;
    private int houseBlocksCount;
    private int population;

    public CityDTO() {
    }

    public CityDTO(Long id, String name, LocationDTO location, int houseBlocksCount, int population) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.houseBlocksCount = houseBlocksCount;
        this.population = population;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocationDTO getLocation() { return location; }
    public void setLocation(LocationDTO location) { this.location = location; }
    public int getHouseBlocksCount() { return houseBlocksCount; }
    public void setHouseBlocksCount(int houseBlocksCount) { this.houseBlocksCount = houseBlocksCount; }
    public int getPopulation() { return population; }
    public void setPopulation(int population) { this.population = population; }
}
