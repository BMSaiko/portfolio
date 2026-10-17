package pt.ipp.isep.dei.domain.locomotive;

public class LocomotiveType {
    private String name;
    private double speed;
    private double power;
    private double cost;

    public LocomotiveType(String name, double speed, double power, double cost) {
        this.name = name;
        this.speed = speed;
        this.power = power;
        this.cost = cost;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
