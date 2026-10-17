package pt.ipp.isep.dei.domain.stationtype;

public enum StationType {
    DEPOT,
    STATION,
    TERMINAL;

    public int getRadius() { return 0; }
    public double getCost() { return 0.0; }
}
