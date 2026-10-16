package pt.ipp.isep.dei.dto;

/**
 * DTO for Industry Subtype information
 */
public class IndustrySubtypeDTO {
    private Long id;
    private String name;
    private Long typeId;
    private java.util.List<String> inputResources;
    private java.util.List<String> outputProducts;

    public IndustrySubtypeDTO() {
    }

    public IndustrySubtypeDTO(Long id, String name, Long typeId, 
                             java.util.List<String> inputResources, 
                             java.util.List<String> outputProducts) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.inputResources = inputResources;
        this.outputProducts = outputProducts;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }
    public java.util.List<String> getInputResources() { return inputResources; }
    public void setInputResources(java.util.List<String> inputResources) { this.inputResources = inputResources; }
    public java.util.List<String> getOutputProducts() { return outputProducts; }
    public void setOutputProducts(java.util.List<String> outputProducts) { this.outputProducts = outputProducts; }
}
