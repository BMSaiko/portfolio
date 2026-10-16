package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.service.AuthenticationService;
import pt.ipp.isep.dei.auth.UserSession;
import pt.ipp.isep.dei.repository.GameRepository;
import pt.ipp.isep.dei.repository.MapRepository;
import pt.ipp.isep.dei.repository.IndustryTypeRepository;
import pt.ipp.isep.dei.repository.IndustryRepository;
import pt.ipp.isep.dei.service.IndustryService;
import pt.ipp.isep.dei.mapper.MapMapper;
import pt.ipp.isep.dei.mapper.IndustryTypeMapper;
import pt.ipp.isep.dei.mapper.IndustrySubtypeMapper;
import pt.ipp.isep.dei.dto.MapDTO;
import pt.ipp.isep.dei.dto.IndustryTypeDTO;
import pt.ipp.isep.dei.dto.IndustrySubtypeDTO;
import pt.ipp.isep.dei.dto.AddIndustryRequestDTO;
import java.util.List;

public class AddIndustryController {
    private AuthenticationService authService;
    private GameRepository gameRepository;
    private IndustryService industryService;
    private MapMapper mapMapper;
    private IndustryTypeMapper industryTypeMapper;
    private IndustrySubtypeMapper industrySubtypeMapper;

    public AddIndustryController() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets current user role
     */
    public String getCurrentUserRole() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets list of available maps
     */
    public List<MapDTO> getMapList() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets list of industry types
     */
    public List<IndustryTypeDTO> getIndustryTypeList() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Gets list of industry subtypes for a given type
     */
    public List<IndustrySubtypeDTO> getIndustrySubtypeList(Long typeId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Adds an industry to the map
     */
    public void addIndustry(AddIndustryRequestDTO request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
