package pt.ipp.isep.dei.domain.scenario;

import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.domain.scenario.IndustrySubtype;
import java.util.List;

/**
 * Enhanced MixedIndustry
 */
public class MixedIndustry extends Industry {
    private List<String> capabilities;

    public MixedIndustry() {
        super();
    }

    public MixedIndustry(String name, Location location, IndustrySubtype industrySubtype) {
        super(name, location, industrySubtype);
    }

    public MixedIndustry(String name, Location location, IndustrySubtype industrySubtype, List<String> capabilities) {
        super(name, location, industrySubtype);
        this.capabilities = capabilities;
    }

    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
}