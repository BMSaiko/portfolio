package pt.isep.dei.esinf.domain;


/**
 * represents information about a route segment
 */
public class SegmentInfo {
    public double lengthKm;
    private final String segmentId;
    private final String startStation;
    private final String endStation;
    private final double speedLimitKmh;
    private final double locomotiveMaxSpeed;
    private final double effectiveSpeed;
    private final double timeMinutes;
    private final boolean isElectrified;
    private final String locomotiveType;

    public SegmentInfo(String segmentId, String startStation, String endStation, double lengthKm, double speedLimitKmh, double locomotiveMaxSpeed, double effectiveSpeed, double timeMinutes, boolean isElectrified, String locomotiveType) {
        this.segmentId = segmentId;
        this.startStation = startStation;
        this.endStation = endStation;
        this.lengthKm = lengthKm;
        this.speedLimitKmh = speedLimitKmh;
        this.locomotiveMaxSpeed = locomotiveMaxSpeed;
        this.effectiveSpeed = effectiveSpeed;
        this.timeMinutes = timeMinutes;
        this.isElectrified = isElectrified;
        this.locomotiveType = locomotiveType;
    }

    public double getTimeMinutes() {
        return timeMinutes;
    }

    public void display(int segmentNumber) {
        System.out.printf("  %d. %s → %s\n", segmentNumber, startStation, endStation);
        System.out.printf("     Distance: %.2f m\n", lengthKm);
        System.out.printf("     Speed limit: %.0f km/h | Locomotive max: %.0f km/h\n", speedLimitKmh, locomotiveMaxSpeed);
        System.out.printf("     Effective speed: %.0f km/h\n", effectiveSpeed);
        System.out.printf("     Travel time: %.2f minutes (%.2f hours)\n", timeMinutes, timeMinutes / 60);
        System.out.printf("     Electrified: %s | Type: %s\n", isElectrified ? "Yes" : "No", locomotiveType);
        System.out.println();
    }
}



