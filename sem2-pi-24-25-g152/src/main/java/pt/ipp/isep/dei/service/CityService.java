package pt.ipp.isep.dei.service;

import pt.ipp.isep.dei.domain.locomotive.Cargo;
import pt.ipp.isep.dei.domain.map.Station;
import pt.ipp.isep.dei.domain.map.City;
import pt.ipp.isep.dei.domain.map.Map;
import pt.ipp.isep.dei.dto.AddCityRequestDTO;
import pt.ipp.isep.dei.repository.CityRepository;
import pt.ipp.isep.dei.domain.validator.CityValidator;
import java.util.List;

public class CityService {
    private CityRepository cityRepository;
    private CityValidator cityValidator;

    public CityService() {
    }

    public CityService(CityRepository cityRepository, CityValidator cityValidator) {
        this.cityRepository = cityRepository;
        this.cityValidator = cityValidator;
    }

    /**
     * Validates city placement on map
     */
    public boolean validateCityPlacement(Map map, AddCityRequestDTO cityData) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Creates a city from request data
     */
    public City createCity(AddCityRequestDTO request, Map map) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Checks if city name is unique in the map
     */
    public boolean isNameUniqueInMap(String name, Long mapId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Getters and setters
    public CityRepository getCityRepository() { return cityRepository; }
    public void setCityRepository(CityRepository cityRepository) { this.cityRepository = cityRepository; }
    public CityValidator getCityValidator() { return cityValidator; }
    public void setCityValidator(CityValidator cityValidator) { this.cityValidator = cityValidator; }

    public List<Cargo> generateCargoForStation(Station station) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
