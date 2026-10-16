package pt.isep.dei.esinf.domain.train;

public class WagonModel {
    private int id;
    private String name;
    private int tare; // empty weight (wagon_model_weight)
    private int payload; // max cargo (wagon_model_payload)
    private int maximumSpeed;

    public WagonModel(int id, String name, int tare, int payload, int maximumSpeed) {
        this.id = id;
        this.name = name;
        this.tare = tare;
        this.payload = payload;
        this.maximumSpeed = maximumSpeed;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getTare() { return tare; }
    public int getPayload() { return payload; }
    public int getMaximumSpeed() { return maximumSpeed; }
}
