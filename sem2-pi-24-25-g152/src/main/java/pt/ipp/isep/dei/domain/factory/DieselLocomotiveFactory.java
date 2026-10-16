package pt.ipp.isep.dei.domain.factory;

import pt.ipp.isep.dei.domain.locomotive.Locomotive;
import pt.ipp.isep.dei.dto.LocomotiveSpecsDTO;

public class DieselLocomotiveFactory implements LocomotiveFactory {
    @Override
    public Locomotive createLocomotive(LocomotiveSpecsDTO specs) {
        return null;
    }

    @Override
    public String getSupportedType() {
        return null;
    }
}
