package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.dto.PathCostDTO;

import java.util.List;


public class PathMapper implements Mapper<PathCostDTO> {

    @Override
    public PathCostDTO toDTO(PathCostDTO type) {
        return null;
    }

    @Override
    public List<PathCostDTO> toDTOList(List<PathCostDTO> types) {
        return List.of();
    }
}
