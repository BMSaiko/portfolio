package pt.isep.dei.esinf.domain.results;

import pt.isep.dei.esinf.domain.SegmentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * travel time calculation result
 */

public class TravelTimeResult {
    private final String originStation;
    private final String destinationStation;
    private final String locomotiveId;
    private final List<SegmentInfo> segments;

    public TravelTimeResult(String originStation, String destinationStation, String locomotiveId) {
        this.originStation = originStation;
        this.destinationStation = destinationStation;
        this.locomotiveId = locomotiveId;
        this.segments = new ArrayList<>();
    }

    public void addSegment(SegmentInfo segment) {
        segments.add(segment);
    }

    public void display() {
        if (segments.isEmpty()) {
            System.out.println("\n✗ No direct connection found between stations.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════╗");
        System.out.println("║           TRAVEL TIME CALCULATION              ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        System.out.println("Route: " + originStation + " → " + destinationStation);
        System.out.println("Locomotive: " + locomotiveId);
        System.out.println("\n" + "=".repeat(50));
        System.out.println("SEGMENTS (in order of traversal):");
        System.out.println("=".repeat(50) + "\n");

        int segmentNumber = 1;
        for (SegmentInfo segment : segments) {
            segment.display(segmentNumber++);
        }

        double totalTime = segments.stream().mapToDouble(SegmentInfo::getTimeMinutes).sum();
        double totalDistance = segments.stream().mapToDouble(s -> s.lengthKm).sum();

        System.out.println("=".repeat(50));
        System.out.printf("TOTAL DISTANCE: %.2f m\n", totalDistance);
        System.out.printf("TOTAL ESTIMATED TRAVEL TIME: %.2f minutes (%.2f hours)\n", totalTime, totalTime / 60);
        System.out.println("=".repeat(50));
        System.out.println("\nNote: Acceleration assumed instantaneous (simplified model)");
    }
}
