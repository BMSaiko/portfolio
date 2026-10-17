package pt.ipp.isep.dei.domain.locomotive;

import java.util.List;

public class FreightCarriage extends Carriage {
    private String cargoType;
    private String loadingMechanism;

    public FreightCarriage(String id, double weight, double capacity, double price,
                           String cargoType, String loadingMechanism) {
        super(id, weight, capacity, price);
    }

    @Override
    public String getCargoType() {
        return null;
    }

    @Override
    public double getLoadingTime() {
        return 0.0;
    }

    @Override
    public List<String> getSpecialRequirements() {
        return null;
    }

    @Override
    public double getRevenuePotential() {
        return 0.0;
    }

    public String getLoadingMechanism() {
        return null;
    }
}
