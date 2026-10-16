package pt.isep.dei.esinf.domain.train;

public class Locomotive {
    private int number;
    private String name;
    private LocomotiveModel model;

    public Locomotive(int number, String name, LocomotiveModel model) {
        this.number = number;
        this.name = name;
        this.model = model;
    }

    public int getNumber() { return number; }
    public String getName() { return name; }
    public LocomotiveModel getModel() { return model; }
}

