package pt.isep.dei.esinf.domain.railway;

import java.time.LocalDateTime;

public class SegmentPassage {
    private LineSegment segment;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private int travelSpeedKmh;
    private String crossingInfo;

    public SegmentPassage(LineSegment segment, LocalDateTime arrivalTime,
                          LocalDateTime departureTime, int travelSpeedKmh,
                          String crossingInfo) {
        this.segment = segment;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.travelSpeedKmh = travelSpeedKmh;
        this.crossingInfo = crossingInfo;
    }

    public LineSegment getSegment() { return segment; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public int getTravelSpeedKmh() { return travelSpeedKmh; }
    public String getCrossingInfo() { return crossingInfo; }
}