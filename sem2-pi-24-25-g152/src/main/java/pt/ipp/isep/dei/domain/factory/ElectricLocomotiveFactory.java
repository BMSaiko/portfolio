package pt.ipp.isep.dei.domain.factory;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;
import pt.ipp.isep.dei.dto.LocomotiveSpecsDTO;

public class ElectricLocomotiveFactory implements LocomotiveFactory {
    @Override
    public Locomotive createLocomotive(LocomotiveSpecsDTO specs) {
        return null;
    }

    @Override
    public String getSupportedType() {
        return null;
    }
}