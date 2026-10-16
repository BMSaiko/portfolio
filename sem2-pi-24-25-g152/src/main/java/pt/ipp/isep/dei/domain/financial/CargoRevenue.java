package pt.ipp.isep.dei.domain.financial;

import java.util.ArrayList;
import java.util.List;

import pt.ipp.isep.dei.domain.locomotive.CargoDelivery;

public class CargoRevenue {
    private int year;
    private int totalDeliveries;
    private double totalRevenue;
    private List<CargoDelivery> cargoDeliveries;

    public CargoRevenue(int year) {
        this.year = year;
        this.cargoDeliveries = new ArrayList<>();
    }

    public void calculateFromDeliveries(List<CargoDelivery> deliveries) {
        // TODO: Implement calculation from deliveries
    }
}
