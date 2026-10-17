package pt.ipp.isep.dei.domain.us13;

import java.util.Objects;

import pt.ipp.isep.dei.domain.locomotive.Cargo;

import java.util.List;

/**
 * Represents a station in the railway network.
 */
public class Station {
    private final String name;
    private final StationType type;
    private int id;
    private String location;
    private List<Cargo> cargoes;
    private String cargo;

    
    /**
     * Creates a new station with the specified name.
     * The type is determined from the prefix in the name.
     * 
     * @param name the name of the station
     * @param id 
     */
    public Station(String name, int id) {
        this.id = id;
        this.name = name;
        this.type = StationType.fromName(name);
        this.location = location;
        this.cargo = cargo;
    }
    
    /**
     * Gets the name of the station.
     * 
     * @return the station name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the type of the station.
     * 
     * @return the station type
     */
    public StationType getType() {
        return type;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return Objects.equals(name, station.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}