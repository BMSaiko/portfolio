package pt.isep.dei.esinf.domain.train;

public class SelectableVehicle {
    private final String id;
    private final String name;
    private final boolean inTransit;
    private final String locationInfo;
    private final double distance;

    public SelectableVehicle(String id, String name, String inTransit, String locationInfo, double distance) {
        this.id = id;
        this.name = name;
        this.inTransit = "Sim".equalsIgnoreCase(inTransit);
        this.locationInfo = locationInfo;
        this.distance = distance;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isInTransit() { return inTransit; }
    public String getLocationInfo() { return locationInfo; }
    public double getDistance() { return distance; }

    @Override
    public String toString() {
        String status = this.inTransit ? "In Transit" : "Parked";
        String info = this.inTransit ? "Destiny" : "Location";
        String dist = this.inTransit ? "" : String.format(" (Distance: %.1f)", distance);

        return String.format(" - %s: %s (%s) | %s: %s%s",
                status, id, name, info, locationInfo, dist);
    }
}