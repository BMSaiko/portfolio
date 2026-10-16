package pt.isep.dei.esinf.domain.railway;

import java.util.ArrayList;
import java.util.List;

public class TrainPath {
    private int id;
    private List<Facility> facilities; // ordered by path_facility_order
    private List<LineSegment> segments;

    public TrainPath(int id) {
        this.id = id;
        this.facilities = new ArrayList<>();
        this.segments = new ArrayList<>();
    }

    public int getId() { return id; }
    public List<Facility> getFacilities() { return facilities; }
    public List<LineSegment> getSegments() { return segments; }

    public void addFacility(Facility facility) {
        facilities.add(facility);
    }

    public void addSegment(LineSegment segment) {
        segments.add(segment);
    }

    public Facility getOrigin() {
        return facilities.isEmpty() ? null : facilities.get(0);
    }

    public Facility getDestination() {
        return facilities.isEmpty() ? null : facilities.get(facilities.size() - 1);
    }

    public double getTotalDistanceKm() {
        return segments.stream().mapToInt(LineSegment::getLength).sum() / 1000.0;
    }
}
