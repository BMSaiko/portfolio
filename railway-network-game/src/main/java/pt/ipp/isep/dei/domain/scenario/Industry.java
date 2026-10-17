package pt.ipp.isep.dei.domain.scenario;
import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.domain.scenario.IndustrySubtype;

public class Industry {
    protected Long id;
    private String name;
    private Location location;
    private String category;
    protected IndustrySubtype industrySubtype;

    public Industry() {
    }

    public Industry(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public Industry(String name, Location location, IndustrySubtype industrySubtype) {
        this.name = name;
        this.location = location;
        this.industrySubtype = industrySubtype;
    }

     /**
     * Validates the industry data
     */
    public boolean validateData() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public IndustrySubtype getIndustrySubtype() { return industrySubtype; }
    public void setIndustrySubtype(IndustrySubtype industrySubtype) { this.industrySubtype = industrySubtype; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
