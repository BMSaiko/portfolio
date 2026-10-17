package pt.ipp.isep.dei.mapper;

import java.util.List;
import java.util.stream.Collectors;

import pt.ipp.isep.dei.domain.scenario.IndustryType;
import pt.ipp.isep.dei.dto.IndustryTypeDTO;

public class IndustryTypeMapper implements Mapper<IndustryTypeDTO> {

    @Override
    public IndustryTypeDTO toDTO(IndustryTypeDTO type) {
        return null;
    }

    @Override
    public List<IndustryTypeDTO> toDTOList(List<IndustryTypeDTO> types) {
        return List.of();
    }
}
