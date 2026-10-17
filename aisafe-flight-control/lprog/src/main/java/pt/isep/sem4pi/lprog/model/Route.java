package pt.isep.sem4pi.lprog.model;

public class Route {
    private String origin;
    private String destination;
    private int line;

    public Route(String origin, String destination, int line) {
        this.origin = origin;
        this.destination = destination;
        this.line = line;
    }

    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public int getLine() { return line; }
}