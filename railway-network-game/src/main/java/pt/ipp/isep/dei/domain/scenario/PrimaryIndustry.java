package pt.ipp.isep.dei.domain.scenario;

import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.domain.scenario.IndustrySubtype;
/**
 * Represents a primary industry that generates resources.
 */
public class PrimaryIndustry extends Industry {
    private double outputRate;

    public PrimaryIndustry() {
        super();
    }

    public PrimaryIndustry(String name, Location location, IndustrySubtype industrySubtype) {
        super(name, location, industrySubtype);
    }

    public PrimaryIndustry(String name, Location location, IndustrySubtype industrySubtype, double outputRate) {
        super(name, location, industrySubtype);
        this.outputRate = outputRate;
    }

    public double getOutputRate() { return outputRate; }
    public void setOutputRate(double outputRate) { this.outputRate = outputRate; }
}