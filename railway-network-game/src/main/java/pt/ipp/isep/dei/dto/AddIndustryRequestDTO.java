package pt.ipp.isep.dei.dto;

/**
 * DTO for Add Industry Request
 */
public class AddIndustryRequestDTO {
    private Long mapId;
    private Long industryTypeId;
    private Long industrySubtypeId;
    private int x;
    private int y;

    public AddIndustryRequestDTO() {
    }

    public AddIndustryRequestDTO(Long mapId, Long industryTypeId, Long industrySubtypeId, int x, int y) {
        this.mapId = mapId;
        this.industryTypeId = industryTypeId;
        this.industrySubtypeId = industrySubtypeId;
        this.x = x;
        this.y = y;
    }

    // Getters and setters
    public Long getMapId() { return mapId; }
    public void setMapId(Long mapId) { this.mapId = mapId; }
    public Long getIndustryTypeId() { return industryTypeId; }
    public void setIndustryTypeId(Long industryTypeId) { this.industryTypeId = industryTypeId; }
    public Long getIndustrySubtypeId() { return industrySubtypeId; }
    public void setIndustrySubtypeId(Long industrySubtypeId) { this.industrySubtypeId = industrySubtypeId; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}