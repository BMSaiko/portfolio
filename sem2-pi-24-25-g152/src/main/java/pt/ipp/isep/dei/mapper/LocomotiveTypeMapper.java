package pt.ipp.isep.dei.mapper;

import java.util.List;
import java.util.stream.Collectors;

import pt.ipp.isep.dei.domain.locomotive.LocomotiveType;
import pt.ipp.isep.dei.dto.LocomotiveTypeDTO;

public class LocomotiveTypeMapper implements Mapper<LocomotiveTypeDTO> {

    @Override
    public LocomotiveTypeDTO toDTO(LocomotiveTypeDTO type) {
        return null;
    }

    @Override
    public List<LocomotiveTypeDTO> toDTOList(List<LocomotiveTypeDTO> types) {
        return List.of();
    }
}
