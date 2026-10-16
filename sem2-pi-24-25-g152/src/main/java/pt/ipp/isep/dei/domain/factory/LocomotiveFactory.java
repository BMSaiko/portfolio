package pt.ipp.isep.dei.domain.factory;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;
import pt.ipp.isep.dei.dto.LocomotiveSpecsDTO;

public interface LocomotiveFactory {
    Locomotive createLocomotive(LocomotiveSpecsDTO specs);
    String getSupportedType();
}
