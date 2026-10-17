package pt.ipp.isep.dei.domain.factory;

import java.util.List;

import pt.ipp.isep.dei.domain.scenario.MixedIndustry;
import pt.ipp.isep.dei.domain.scenario.PrimaryIndustry;
import pt.ipp.isep.dei.domain.scenario.TransformingIndustry;
import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.domain.scenario.Industry;
import pt.ipp.isep.dei.domain.scenario.IndustrySubtype;

/**
 * Factory for creating industries.
 */
public class IndustryFactory {
    public IndustryFactory() {
    }

    public List<String> getIndustryTypes(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<String> getIndustrySubtypes(){
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Creates an industry based on subtype
     */
    public Industry createIndustry(IndustrySubtype subtype, Location location) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public PrimaryIndustry createPrimaryIndustry(String subtype, Location location) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public TransformingIndustry createTransformingIndustry(String subtype, Location location) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public MixedIndustry createMixedIndustry(String subtype, Location location) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}