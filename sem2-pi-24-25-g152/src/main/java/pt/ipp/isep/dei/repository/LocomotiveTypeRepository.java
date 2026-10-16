package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.locomotive.LocomotiveType;

import java.util.List;
import java.util.Optional;

public interface LocomotiveTypeRepository {
    List<LocomotiveType> getAllLocomotiveTypes();
    Optional<LocomotiveType> getLocomotiveTypeByName(String name);
}
