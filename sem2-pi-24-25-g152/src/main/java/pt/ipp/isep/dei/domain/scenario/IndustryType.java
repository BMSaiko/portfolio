package pt.ipp.isep.dei.domain.scenario;

public class IndustryType {
    private String name;
    private double productionRate;
    private double consumptionRate;

    public IndustryType(String name, double productionRate, double consumptionRate) {
        this.name = name;
        this.productionRate = productionRate;
        this.consumptionRate = consumptionRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProductionRate() {
        return productionRate;
    }

    public void setProductionRate(double productionRate) {
        this.productionRate = productionRate;
    }

    public double getConsumptionRate() {
        return consumptionRate;
    }

    public void setConsumptionRate(double consumptionRate) {
        this.consumptionRate = consumptionRate;
    }
}
