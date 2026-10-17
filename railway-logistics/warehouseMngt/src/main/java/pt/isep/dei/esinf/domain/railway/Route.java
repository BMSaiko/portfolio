package pt.isep.dei.esinf.domain.railway;

import pt.isep.dei.esinf.domain.stations.Station;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private int id;
    private String name;
    private String type; // SIMPLE or COMPLEX
    private Station origin;
    private Station destination;
    private List<LineSegment> segments;

    public Route(int id, String name, String type, Station origin, Station destination) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.origin = origin;
        this.destination = destination;
        this.segments = new ArrayList<>();
    }


    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public Station getOrigin() { return origin; }
    public Station getDestination() { return destination; }
    public List<LineSegment> getSegments() { return segments; }

    public void addSegment(LineSegment segment) {
        segments.add(segment);
    }

    public int getTotalDistance() {
        return segments.stream().mapToInt(LineSegment::getLength).sum();
    }

    public boolean isSimple() {
        return "SIMPLE".equalsIgnoreCase(type);
    }
}
