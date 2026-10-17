package pt.ipp.isep.dei.domain.map;

import pt.ipp.isep.dei.domain.map.City;
import java.util.List;
import java.util.ArrayList;

/**
 * Enhanced Map domain class for City functionality
 */
public class CityMap extends Map {
    private List<City> cities;

    public CityMap(String name) {
        super(name);
        this.cities = new ArrayList<>();
    }

    public CityMap(String name, int width, int height) {
        super(name, width, height);
        this.cities = new ArrayList<>();
    }

    /**
     * Adds a city to the map
     */
    public boolean addCity(City city) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets all city names in the map
     */
    public List<String> getCityNames() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks if location is valid for city placement
     */
    public boolean isValidLocation(Location location) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks if there's an element at the given location
     */
    public boolean hasElementAt(Location location) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Getters and setters
    public List<City> getCities() { return cities; }
    public void setCities(List<City> cities) { this.cities = cities; }
}
