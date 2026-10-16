package pt.isep.dei.esinf.domain.train;

public class LocomotiveModel {
    private int id;
    private String name;
    private int power; // kW
    private int weight; // tons
    private int maximumSpeed; // km/h
    private boolean isElectric;

    public LocomotiveModel(int id, String name, int power, int weight,
                           int maximumSpeed, boolean isElectric) {
        this.id = id;
        this.name = name;
        this.power = power;
        this.weight = weight;
        this.maximumSpeed = maximumSpeed;
        this.isElectric = isElectric;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getPower() { return power; }
    public int getWeight() { return weight; }
    public int getMaximumSpeed() { return maximumSpeed; }
    public boolean isElectric() { return isElectric; }
}
