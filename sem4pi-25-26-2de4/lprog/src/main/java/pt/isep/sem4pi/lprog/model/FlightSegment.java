package pt.isep.sem4pi.lprog.model;

public class FlightSegment {
    private String mode;
    private Coordinate start;
    private Coordinate end;
    private double windSpeed;
    private int line;

    public FlightSegment(String mode, Coordinate start, Coordinate end, double windSpeed, int line) {
        this.mode = mode;
        this.start = start;
        this.end = end;
        this.windSpeed = windSpeed;
        this.line = line;
    }

    public String getMode() { return mode; }
    public Coordinate getStart() { return start; }
    public Coordinate getEnd() { return end; }
    public double getWindSpeed() { return windSpeed; }
    public int getLine() { return line; }
}