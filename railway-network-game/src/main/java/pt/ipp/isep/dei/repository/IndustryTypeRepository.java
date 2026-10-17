package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.scenario.IndustryType;
import pt.ipp.isep.dei.domain.scenario.IndustrySubtype;
import java.util.List;

public class IndustryTypeRepository {
    public IndustryTypeRepository() {
    }

    /**
     * Gets all industry types
     */
    public List<IndustryType> getAllIndustryTypes() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Finds industry type by ID
     */
    public IndustryType findById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets subtypes by type ID
     */
    public List<IndustrySubtype> getSubtypesByTypeId(Long typeId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Finds subtype by ID
     */
    public IndustrySubtype findSubtypeById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
