package pt.ipp.isep.dei.mapper;

import pt.ipp.isep.dei.dto.CargoDTO;

import java.util.List;

public class CargoMapper implements Mapper<CargoDTO> {

    @Override
    public CargoDTO toDTO(CargoDTO cargo) {
        // Implement the conversion logic from Cargo to CargoDTO
        return null; // Placeholder return statement
    }

    @Override
    public List<CargoDTO> toDTOList(List<CargoDTO> cargos) {
        // Implement the conversion logic from List of Cargo to List of CargoDTO
        return List.of(); // Placeholder return statement
    }
}
