package pt.ipp.isep.dei.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced MapDTO for Add City functionality
 */
public class CityMapDTO extends MapDTO {
    private List<String> existingCityNames;

    public CityMapDTO(long id, String name, int width, int height) {
        super(id, name, width, height);
        this.existingCityNames = new ArrayList<>();
    }

    public List<String> getExistingCityNames() {
        return new ArrayList<>(existingCityNames);
    }

    public void setExistingCityNames(List<String> existingCityNames) {
        this.existingCityNames = existingCityNames;
    }

    public void addExistingCityName(String cityName) {
        this.existingCityNames.add(cityName);
    }
}
