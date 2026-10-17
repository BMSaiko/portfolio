package pt.ipp.isep.dei.domain.scenario;

import java.util.List;
import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.domain.scenario.IndustrySubtype;
/**
 * Represents a transforming industry that transforms resources into products.
 */
public class TransformingIndustry extends Industry {
    private List<String> inputResources;
    private String outputProduct;
    private double transformationRatio;

     public TransformingIndustry() {
        super();
    }

     public TransformingIndustry(String name, Location location, IndustrySubtype industrySubtype) {
        super(name, location, industrySubtype);
    }

        public TransformingIndustry(String name, Location location, IndustrySubtype industrySubtype, double transformationRatio) {
        super(name, location, industrySubtype);
        this.transformationRatio = transformationRatio;
    }
    
    public List<String> getInputResources() {
        return inputResources;
    }

    public String getOutputProduct() {
        return outputProduct;
    }

    public double getTransformationRatio() { return transformationRatio; }
    public void setTransformationRatio(double transformationRatio) { this.transformationRatio = transformationRatio; }

}