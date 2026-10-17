package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.scenario.IndustrySubtype;
import pt.ipp.isep.dei.dto.IndustrySubtypeDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for IndustrySubtype entities and DTOs
 */
public class IndustrySubtypeMapper implements Mapper<IndustrySubtypeDTO> {

    @Override
    public IndustrySubtypeDTO toDTO(IndustrySubtypeDTO type) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<IndustrySubtypeDTO> toDTOList(List<IndustrySubtypeDTO> types) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Converts IndustrySubtype domain object to DTO
     */
    public IndustrySubtypeDTO subtypeToDTO(IndustrySubtype subtype) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Converts list of IndustrySubtype domain objects to DTOs
     */
    public List<IndustrySubtypeDTO> subtypeToDTOList(List<IndustrySubtype> subtypes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}