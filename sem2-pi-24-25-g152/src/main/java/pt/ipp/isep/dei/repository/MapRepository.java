package pt.ipp.isep.dei.repository;
import pt.ipp.isep.dei.domain.map.City;
import pt.ipp.isep.dei.domain.us13.GameMap;
import pt.ipp.isep.dei.domain.map.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing maps.
 */
public class MapRepository implements IMapRepository {
    private Map currentMap;

    private final List<GameMap> gameMaps = new ArrayList<>();
    private final List<Map> maps = new ArrayList<>();
    private Long nextId = 1L;
    
    public MapRepository() {
    }

    /**
     * Saves a map to the repository.
     * 
     * @param map the map to save
     */
    public void saveGameMap(GameMap map) {
      
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Checks if a map with the given name exists.
     * 
     * @param name the map name to check
     * @return true if a map with the given name exists, false otherwise
     */
    public boolean existsByName(String name) {
      
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets all maps in the repository.
     * 
     * @return an unmodifiable list of all maps
     */
    public List<GameMap> getAllMaps() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets a map by its name.
     * 
     * @param name the name of the map
     * @return the map with the given name, or null if no such map exists
     */
    public GameMap getByName(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Returns all Map objects in the repository.
     * 
     * @return a list of all Map objects
     */
    public List<Map> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Finds a Map by its ID.
     * 
     * @param id the ID of the map to find
     * @return the map with the given ID, or null if no such map exists
     */
    public Map findById(Long id) {
       
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Saves a Map object to the repository.
     * If the map doesn't have an ID, it assigns one.
     * 
     * @param map the map to save
     */
    public void save(Map map) {
  
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Alias for save(Map) for consistency.
     * 
     * @param map the map to save
     */
    public void saveMap(Map map) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map getCurrentMap() {
       
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public City findClosestCity(int x, int y) {
      
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public boolean validatePosition(int x, int y, String stationType) {
     
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void setCurrentMap(Map currentMap) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}