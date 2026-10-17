package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.map.City;
import pt.ipp.isep.dei.domain.map.Location;
import pt.ipp.isep.dei.dto.CityDTO;
import pt.ipp.isep.dei.dto.LocationDTO;
import java.util.List;

/**
 * Mapper for City entities and DTOs
 */
public class CityMapper implements Mapper<CityDTO> {

    public CityMapper() {
    }

    @Override
    public CityDTO toDTO(CityDTO type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<CityDTO> toDTOList(List<CityDTO> types) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Converts City domain object to DTO
     */
    public CityDTO toDTO(City city) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Converts Location domain object to DTO
     */
    public LocationDTO locationToDTO(Location location) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Converts LocationDTO to domain object
     */
    public Location locationFromDTO(LocationDTO dto) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
