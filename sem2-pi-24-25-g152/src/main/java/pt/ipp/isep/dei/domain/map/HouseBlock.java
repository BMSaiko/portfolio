package pt.ipp.isep.dei.domain.map;

public class HouseBlock {
    private Long id;
    private Location location;
    private int populationValue;

      public HouseBlock() {
    }

    public HouseBlock(Location location) {
        this.location = location;
    }

    public HouseBlock(Location location, int populationValue) {
        this.location = location;
        this.populationValue = populationValue;
    }

    /**
     * Gets the house block location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the population value of this house block
     */
    public int getPopulationValue() {
        return populationValue;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setLocation(Location location) { this.location = location; }
    public void setPopulationValue(int populationValue) { this.populationValue = populationValue; }
}
