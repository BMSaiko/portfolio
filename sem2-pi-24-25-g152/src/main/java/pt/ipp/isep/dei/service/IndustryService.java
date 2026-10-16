package pt.ipp.isep.dei.service;

import pt.ipp.isep.dei.domain.locomotive.Cargo;
import pt.ipp.isep.dei.domain.map.Station;
import pt.ipp.isep.dei.domain.scenario.Industry;
import pt.ipp.isep.dei.domain.scenario.IndustrySubtype;
import pt.ipp.isep.dei.domain.map.Map;
import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.dto.AddIndustryRequestDTO;
import pt.ipp.isep.dei.repository.IndustryRepository;
import pt.ipp.isep.dei.domain.factory.IndustryFactory;

import java.util.List;

public class IndustryService {
    public List<Cargo> generateCargoForStation(Station station) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private IndustryRepository industryRepository;
    private IndustryFactory industryFactory;

     public IndustryService() {
    }

    public IndustryService(IndustryRepository industryRepository, IndustryFactory industryFactory) {
        this.industryRepository = industryRepository;
        this.industryFactory = industryFactory;
    }

    /**
     * Validates industry placement on map
     */
    public boolean validateIndustryPlacement(Map map, Location location) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates an industry from request data
     */
    public Industry createIndustry(AddIndustryRequestDTO request, Map map, IndustrySubtype industrySubtype) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Getters and setters
    public IndustryRepository getIndustryRepository() { return industryRepository; }
    public void setIndustryRepository(IndustryRepository industryRepository) { this.industryRepository = industryRepository; }
    public IndustryFactory getIndustryFactory() { return industryFactory; }
    public void setIndustryFactory(IndustryFactory industryFactory) { this.industryFactory = industryFactory; }

}
