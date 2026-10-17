package pt.ipp.isep.dei.dto;

/**
 * Enhanced IndustryTypeDTO
 */
public class EnhancedIndustryTypeDTO extends IndustryTypeDTO {
    private Long id;
    private String category;

    public EnhancedIndustryTypeDTO() {
        super();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}