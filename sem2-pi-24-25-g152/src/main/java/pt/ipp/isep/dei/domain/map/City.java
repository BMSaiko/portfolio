package pt.ipp.isep.dei.domain.map;


import java.util.ArrayList;
import java.util.List;

public class City {
    private Long id;
    private String name;
    private int population;
    private Location location;
    private Object demandFactors;
    private List<HouseBlock> houseBlocks;

    public City() {
        this.houseBlocks = new ArrayList<>();
    }

    public City(String name, int population, Location location, Object demandFactors) {
        this.name = name;
        this.population = population;
        this.location = location;
        this.demandFactors = demandFactors;
        this.houseBlocks = new ArrayList<>();
    }

    public City(String name, Location location) {
        this.name = name;
        this.location = location;
        this.houseBlocks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public Object getLocation() {
        return location;
    }

    public Object getDemandFactors() {
        return demandFactors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDemandFactors(Object demandFactors) {
        this.demandFactors = demandFactors;
    }

   public List<HouseBlock> getHouseBlocks() {
        return new ArrayList<>(houseBlocks);
    }

    public void addHouseBlock(HouseBlock block) {
        this.houseBlocks.add(block);
    }

    public void addHouseBlocks(List<HouseBlock> blocks) {
        this.houseBlocks.addAll(blocks);
    }

        /**
     * Validates city data
     */
    public boolean validateData() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

     // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setHouseBlocks(List<HouseBlock> houseBlocks) { this.houseBlocks = houseBlocks; }
}
