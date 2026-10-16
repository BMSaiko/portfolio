package pt.ipp.isep.dei.dto;

import java.util.List;

/**
 * DTO for Add City Request
 */
public class AddCityRequestDTO {
    private Long mapId;
    private String name;
    private int x;
    private int y;
    private int houseBlocksCount;
    private String placementMethod;
    private List<LocationDTO> manualBlockLocations;

    public AddCityRequestDTO() {
    }

    public AddCityRequestDTO(Long mapId, String name, int x, int y, int houseBlocksCount, 
                            String placementMethod, List<LocationDTO> manualBlockLocations) {
        this.mapId = mapId;
        this.name = name;
        this.x = x;
        this.y = y;
        this.houseBlocksCount = houseBlocksCount;
        this.placementMethod = placementMethod;
        this.manualBlockLocations = manualBlockLocations;
    }

    // Getters and setters
    public Long getMapId() { return mapId; }
    public void setMapId(Long mapId) { this.mapId = mapId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getHouseBlocksCount() { return houseBlocksCount; }
    public void setHouseBlocksCount(int houseBlocksCount) { this.houseBlocksCount = houseBlocksCount; }
    public String getPlacementMethod() { return placementMethod; }
    public void setPlacementMethod(String placementMethod) { this.placementMethod = placementMethod; }
    public List<LocationDTO> getManualBlockLocations() { return manualBlockLocations; }
    public void setManualBlockLocations(List<LocationDTO> manualBlockLocations) { this.manualBlockLocations = manualBlockLocations; }
}
