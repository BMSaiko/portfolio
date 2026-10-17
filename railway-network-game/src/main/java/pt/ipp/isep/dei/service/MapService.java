package pt.ipp.isep.dei.service;

import java.util.List;
import java.util.stream.Collectors;

import pt.ipp.isep.dei.domain.us13.GameMap;
import pt.ipp.isep.dei.dto.MapDTO;
import pt.ipp.isep.dei.repository.MapRepository;

/**
 * Service for map operations.
 */
public class MapService {
    private final MapRepository mapRepository;
    
    /**
     * Creates a new map service with a new repository.
     */
    public MapService() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Creates a new map service with the specified repository.
     * 
     * @param mapRepository the map repository
     */
    public MapService(MapRepository mapRepository) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Creates a new map with the specified parameters.
     * 
     * @param mapDTO the map data transfer object containing map details
     * @return the created map as a DTO
     * @throws IllegalArgumentException if the map name is not unique or parameters are invalid
     */
    public MapDTO createMap(MapDTO mapDTO) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Checks if a map name is unique.
     * 
     * @param name the name to check
     * @return true if the name is unique, false otherwise
     */
    private boolean isNameUnique(String name) {
      
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets all maps.
     * 
     * @return a list of all maps
     */
    public List<GameMap> getAllMaps() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets all maps as DTOs.
     * 
     * @return a list of all maps as DTOs
     */
    public List<MapDTO> getAllMapsAsDTO() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets a map by its name.
     * 
     * @param name the name of the map
     * @return the map with the given name, or null if no such map exists
     */
    public GameMap getMapByName(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Gets a map by its name as a DTO.
     * 
     * @param name the name of the map
     * @return the map with the given name as a DTO, or null if no such map exists
     */
    public MapDTO getMapByNameAsDTO(String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    /**
     * Converts a GameMap entity to a MapDTO.
     * 
     * @param map the map entity
     * @return the map DTO
     */
    private MapDTO convertToDTO(GameMap map) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}