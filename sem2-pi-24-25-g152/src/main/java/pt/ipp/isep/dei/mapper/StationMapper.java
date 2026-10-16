package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.map.Map;
import pt.ipp.isep.dei.domain.map.Station;
import pt.ipp.isep.dei.dto.StationCreationRequestDTO;
import pt.ipp.isep.dei.dto.StationDTO;
import pt.ipp.isep.dei.dto.StationResponseDTO;

import java.util.List;

public class StationMapper implements Mapper<StationDTO> {

    @Override
    public StationDTO toDTO(StationDTO type) {
        return null;
    }

    @Override
    public List<StationDTO> toDTOList(List<StationDTO> types) {
        return List.of();
    }

    public static StationResponseDTO toDTO(Station station) {
        // Implementation logic goes here
        return null;
    }

    public static Station fromDTO(StationCreationRequestDTO dto, Map map) {
        // Implementation logic goes here
        return null;
    }
}
