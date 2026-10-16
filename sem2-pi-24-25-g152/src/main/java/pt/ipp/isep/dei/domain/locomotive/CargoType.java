package pt.ipp.isep.dei.domain.locomotive;

/**
 * Enhanced CargoType domain class
 */
public class CargoType {
    private Long id;
    private String name;
    private String unit;

    public CargoType() {
    }

    public CargoType(Long id, String name, String unit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}