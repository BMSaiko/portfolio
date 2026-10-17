package pt.ipp.isep.dei.domain.map;

import pt.ipp.isep.dei.domain.scenario.HistoricalEvent;
import pt.ipp.isep.dei.domain.scenario.Industry;
import pt.ipp.isep.dei.domain.map.City;
import pt.ipp.isep.dei.domain.scenario.IndustryType;
import pt.ipp.isep.dei.domain.locomotive.LocomotiveType;
import pt.ipp.isep.dei.domain.scenario.Scenario;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Map {
    private Long id;
    private String name;
    private int width;
    private int height;
    private List<Object> regions;
    private List<Scenario> scenarios;
    private List<Station> stations;
    private List<Industry> industries;
    private List<City> cities;
    

    public Map(String name) {
        this.name = name;
        this.width = 0;
        this.height = 0;
        this.regions = new ArrayList<>();
        this.scenarios = new ArrayList<>();
        this.stations = new ArrayList<>();
        this.industries = new ArrayList<>();
        this.cities = new ArrayList<>();
    }
    
    /**
     * Creates a new map with the specified name, width, and height.
     * 
     * @param name the name of the map
     * @param width the width of the map
     * @param height the height of the map
     */
    public Map(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.regions = new ArrayList<>();
        this.scenarios = new ArrayList<>();
        this.stations = new ArrayList<>();
        this.industries = new ArrayList<>();
        this.cities = new ArrayList<>();
    }

    /**
     * Gets the ID of the map.
     * 
     * @return the map ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the ID of the map.
     * 
     * @param id the map ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public List<Scenario> getScenarios() {
        return new ArrayList<>(scenarios);
    }

    public Scenario createScenario(String scenarioName, Date startTime, Date endTime,
                                   List<LocomotiveType> locomotiveTypes, List<IndustryType> industryTypes,
                                   List<HistoricalEvent> historicalEvents) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getRegions() {
        return regions;
    }

    public void setRegions(List<Object> regions) {
        this.regions = regions;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public List<Industry> getIndustries() {
        return industries;
    }

    public void setIndustries(List<Industry> industries) {
        this.industries = industries;
    }

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public boolean addIndustry(Industry industry) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean addCity(City city) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean isValidLocation(Location location) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean validateStationPosition(int x, int y, String stationType) {
        return true;
    }

    public City findClosestCity(int x, int y) {
        return null;
    }

       /**
     * Checks if there's an element at the given location
     */
    public boolean hasElementAt(Location location) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
}
