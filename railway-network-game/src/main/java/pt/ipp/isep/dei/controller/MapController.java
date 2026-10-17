package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.dto.MapDTO;
import pt.ipp.isep.dei.service.MapService;

import java.util.List;

/**
 * Controller for map operations.
 */
public class MapController {
    private final MapService mapService;
    
    /**
     * Creates a new map controller.
     */
    public MapController() {
        this.mapService = new MapService();
    }    /**
     * Creates a new map with the specified parameters.
     * 
     * @param mapDTO the data transfer object containing map details
     * @return the created map as a DTO
     * @throws IllegalArgumentException if the map name is not unique or parameters are invalid
     */
    public MapDTO createMap(MapDTO mapDTO) {
        if (mapDTO == null) {
            throw new IllegalArgumentException("Map DTO cannot be null");
        }
        return mapService.createMap(mapDTO);
    }
    
    /**
     * Gets all maps.
     * 
     * @return a list of all maps as DTOs
     */
    public List<MapDTO> getAllMaps() {
        return mapService.getAllMapsAsDTO();
    }
    
    /**
     * Gets a map by its name.
     * 
     * @param name the name of the map
     * @return the map with the given name as a DTO, or null if no such map exists
     */
    public MapDTO getMapByName(String name) {
        return mapService.getMapByNameAsDTO(name);
    }
}