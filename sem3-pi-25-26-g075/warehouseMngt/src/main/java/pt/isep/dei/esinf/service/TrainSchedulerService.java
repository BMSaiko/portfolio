package pt.isep.dei.esinf.service;

import pt.isep.dei.esinf.domain.railway.Facility;
import pt.isep.dei.esinf.domain.railway.LineSegment;
import pt.isep.dei.esinf.domain.railway.SegmentPassage;
import pt.isep.dei.esinf.domain.train.*;

import java.time.LocalDateTime;

public class TrainSchedulerService {

    // Constants for scheduling
    private static final int STOP_TIME_MINUTES = 5; // Default stop time at facilities
    private static final int CROSSING_TIME_MINUTES = 10; // Time to wait for crossing on single-track
    private static final double POWER_TO_SPEED_FACTOR = 8.0; // kW/ton to km/h conversion factor

    /**
     * Calculates the complete schedule for a train
     */
    public TrainSchedule calculateSchedule(Train train) {
        TrainSchedule schedule = new TrainSchedule(train);
        LocalDateTime currentTime = train.getDepartureDateTime();

        System.out.println("\n========================================");
        System.out.println("Calculating schedule for: " + train.getName());
        System.out.println("Departure: " + train.getDepartureDateTime());
        System.out.println("========================================");

        // Process each segment in the path
        for (LineSegment segment : train.getPath().getSegments()) {
            // Calculate maximum achievable speed on this segment
            int maxSpeed = calculateMaxSpeed(train, segment);

            // Calculate travel time
            double distanceKm = segment.getLength() / 1000.0;
            double travelTimeHours = distanceKm / maxSpeed;
            int travelTimeMinutes = (int) Math.ceil(travelTimeHours * 60);

            // Arrival time at end of segment
            LocalDateTime arrivalTime = currentTime.plusMinutes(travelTimeMinutes);

            // Determine if stop is needed
            boolean needsStop = determineStopNeeded(segment, train);
            int stopMinutes = needsStop ? STOP_TIME_MINUTES : 0;

            // Add crossing time for single-track segments
            if (segment.isSingleTrack()) {
                stopMinutes += CROSSING_TIME_MINUTES;
            }

            LocalDateTime departureTime = arrivalTime.plusMinutes(stopMinutes);

            // Create crossing info
            String crossingInfo = buildCrossingInfo(segment);

            // Create passage record
            SegmentPassage passage = new SegmentPassage(
                    segment,
                    arrivalTime,
                    departureTime,
                    maxSpeed,
                    crossingInfo
            );

            schedule.addPassage(passage);

            // Update current time for next segment
            currentTime = departureTime;

            // Debug output
            System.out.printf("  %s → %s: %.1f km @ %d km/h = %d min%n",
                    segment.getStartFacility().getName(),
                    segment.getEndFacility().getName(),
                    distanceKm,
                    maxSpeed,
                    travelTimeMinutes);
        }

        System.out.println("Estimated arrival: " + schedule.getEstimatedArrival());
        System.out.println("Total time: " + schedule.getTotalTravelTimeMinutes() + " minutes");

        return schedule;
    }

    /**
     * Calculates the maximum speed the train can achieve on a given segment
     * Considers: track limit, locomotive limit, wagon limit, and power-to-weight ratio
     */
    private int calculateMaxSpeed(Train train, LineSegment segment) {
        // 1. Track speed limit
        int trackLimit = segment.getSpeedLimitKmh();

        // 2. Locomotive maximum speed
        int locoLimit = train.getMaxLocomotiveSpeedKmh();

        // 3. Wagon maximum speed
        int wagonLimit = train.getMaxWagonSpeedKmh();

        // 4. Power-to-weight ratio limit
        // Formula: speed ≈ (total_power_kw / total_weight_tons) * factor
        int totalPowerKw = train.getTotalPowerKw();
        int totalWeightTons = train.getTotalWeightTons();

        if (totalWeightTons == 0) {
            return Math.min(Math.min(trackLimit, locoLimit), wagonLimit);
        }

        double powerToWeightRatio = (double) totalPowerKw / totalWeightTons;
        int powerBasedLimit = (int) (powerToWeightRatio * POWER_TO_SPEED_FACTOR);

        // Return the minimum of all limits
        int maxSpeed = Math.min(trackLimit, locoLimit);
        maxSpeed = Math.min(maxSpeed, wagonLimit);
        maxSpeed = Math.min(maxSpeed, powerBasedLimit);

        // Ensure at least 20 km/h (minimum operational speed)
        return Math.max(maxSpeed, 20);
    }

    /**
     * Determines if the train needs to stop at the end of this segment
     */
    private boolean determineStopNeeded(LineSegment segment, Train train) {
        // Stop at destination
        Facility endFacility = segment.getEndFacility();
        if (endFacility.equals(train.getPath().getDestination())) {
            return true;
        }

        // Stop at intermodal facilities (freight handling)
        if (endFacility.isIntermodal()) {
            return true;
        }

        // Stop at single-track segments (for crossing)
        if (segment.isSingleTrack()) {
            return true;
        }

        return false;
    }

    /**
     * Builds crossing information string for display
     */
    private String buildCrossingInfo(LineSegment segment) {
        if (segment.isSingleTrack()) {
            if (segment.hasSiding()) {
                return "⚠ Single-track with siding - crossing possible";
            } else {
                return "⚠ Single-track - station crossing required";
            }
        } else {
            return "✓ Double-track - no crossing needed";
        }
    }

    /**
     * Analyzes train performance
     */
    public TrainPerformanceAnalysis analyzePerformance(Train train, TrainSchedule schedule) {
        double avgSpeed = train.getPath().getTotalDistanceKm() /
                (schedule.getTotalTravelTimeMinutes() / 60.0);

        double powerToWeightRatio = (double) train.getTotalPowerKw() / train.getTotalWeightTons();

        int singleTrackSegments = (int) train.getPath().getSegments().stream()
                .filter(LineSegment::isSingleTrack)
                .count();

        return new TrainPerformanceAnalysis(
                avgSpeed,
                powerToWeightRatio,
                singleTrackSegments,
                train.getLoadedWagonCount(),
                train.getEmptyWagonCount()
        );
    }

    /**
     * Inner class for performance analysis results
     */
    public static class TrainPerformanceAnalysis {
        private double averageSpeed;
        private double powerToWeightRatio;
        private int singleTrackSegmentCount;
        private int loadedWagons;
        private int emptyWagons;

        public TrainPerformanceAnalysis(double averageSpeed, double powerToWeightRatio,
                                        int singleTrackSegmentCount, int loadedWagons, int emptyWagons) {
            this.averageSpeed = averageSpeed;
            this.powerToWeightRatio = powerToWeightRatio;
            this.singleTrackSegmentCount = singleTrackSegmentCount;
            this.loadedWagons = loadedWagons;
            this.emptyWagons = emptyWagons;
        }

        public double getAverageSpeed() { return averageSpeed; }
        public double getPowerToWeightRatio() { return powerToWeightRatio; }
        public int getSingleTrackSegmentCount() { return singleTrackSegmentCount; }
        public int getLoadedWagons() { return loadedWagons; }
        public int getEmptyWagons() { return emptyWagons; }
    }
}