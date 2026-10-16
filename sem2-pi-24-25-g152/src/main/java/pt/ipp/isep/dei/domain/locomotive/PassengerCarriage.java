package pt.ipp.isep.dei.domain.locomotive;

import java.util.List;

public class PassengerCarriage extends Carriage {
    private int seatCount;
    private String comfortLevel;

    public PassengerCarriage(String id, double weight, double capacity, double purchasePrice,
                             int seatCount, String comfortLevel) {
        super(id, weight, capacity, purchasePrice);
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

    public int getSeatCount() {
        return 0;
    }

    public String getComfortLevel() {
        return null;
    }
}
