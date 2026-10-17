package pt.ipp.isep.dei.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced MapDTO for Add Industry functionality
 */
public class EnhancedMapDTO extends MapDTO {
    private List<String> existingIndustryNames;

    public EnhancedMapDTO(long id, String name, int width, int height) {
        super(id, name, width, height);
        this.existingIndustryNames = new ArrayList<>();
    }

    public List<String> getExistingIndustryNames() {
        return new ArrayList<>(existingIndustryNames);
    }

    public void setExistingIndustryNames(List<String> existingIndustryNames) {
        this.existingIndustryNames = existingIndustryNames;
    }

    public void addExistingIndustryName(String industryName) {
        this.existingIndustryNames.add(industryName);
    }
}