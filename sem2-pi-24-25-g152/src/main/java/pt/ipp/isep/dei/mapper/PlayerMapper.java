package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.domain.Player;
import pt.ipp.isep.dei.dto.PlayerDTO;

import java.util.List;

public class PlayerMapper implements Mapper<PlayerDTO> {
    @Override
    public PlayerDTO toDTO(PlayerDTO type) {
        return null;
    }

    @Override
    public List<PlayerDTO> toDTOList(List<PlayerDTO> types) {
        return List.of();
    }
}
