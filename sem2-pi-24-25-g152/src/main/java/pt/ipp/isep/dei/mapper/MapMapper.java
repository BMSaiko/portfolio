package pt.ipp.isep.dei.mapper;

import java.util.List;
import java.util.stream.Collectors;

import pt.ipp.isep.dei.domain.map.Map;
import pt.ipp.isep.dei.dto.MapDTO;

public class MapMapper implements Mapper<MapDTO> {

    @Override
    public MapDTO toDTO(MapDTO type) {
        return null;
    }

    @Override
    public List<MapDTO> toDTOList(List <MapDTO> types) {
        return List.of();
    }
}
