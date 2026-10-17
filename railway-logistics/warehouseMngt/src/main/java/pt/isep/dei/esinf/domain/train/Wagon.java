package pt.isep.dei.esinf.domain.train;

public class Wagon {
    private String number;
    private WagonModel model;
    private boolean isLoaded;
    private int cargoWeight; // tons

    public Wagon(String number, WagonModel model, boolean isLoaded, int cargoWeight) {
        this.number = number;
        this.model = model;
        this.isLoaded = isLoaded;
        this.cargoWeight = cargoWeight;
    }

    public String getNumber() { return number; }
    public WagonModel getModel() { return model; }
    public boolean isLoaded() { return isLoaded; }
    public int getCargoWeight() { return cargoWeight; }

    public int getTotalWeight() {
        return model.getTare() + (isLoaded ? cargoWeight : 0);
    }
}
