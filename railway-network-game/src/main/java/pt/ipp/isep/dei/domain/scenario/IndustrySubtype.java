package pt.ipp.isep.dei.domain.scenario;

import pt.ipp.isep.dei.domain.locomotive.CargoType;
import java.util.List;

/**
 * Enhanced IndustrySubtype domain class
 */
public class IndustrySubtype {
    private Long id;
    private String name;
    private IndustryType industryType;
    private List<CargoType> inputResources;
    private List<CargoType> outputProducts;

    public IndustrySubtype() {
    }

    public IndustrySubtype(Long id, String name, IndustryType industryType) {
        this.id = id;
        this.name = name;
        this.industryType = industryType;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public IndustryType getIndustryType() { return industryType; }
    public void setIndustryType(IndustryType industryType) { this.industryType = industryType; }
    public List<CargoType> getInputResources() { return inputResources; }
    public void setInputResources(List<CargoType> inputResources) { this.inputResources = inputResources; }
    public List<CargoType> getOutputProducts() { return outputProducts; }
    public void setOutputProducts(List<CargoType> outputProducts) { this.outputProducts = outputProducts; }
}
