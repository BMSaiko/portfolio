package eapli.aisafe.reportmanagement.application;

import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanStatus;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.reportmanagement.domain.ReportPeriod;
import eapli.aisafe.reportmanagement.domain.ReportType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;


/**
 * Concrete report generator for monthly statistics reports.
 * <p>
 * Aggregates flight plan data by month and produces sections:
 * Summary, Fleet, Safety, and Top Routes.
 * </p>
 */
public class MonthlyReportGenerator extends ReportGenerator {

    private static final String SIM_DIR = "reports/simulation/";
    private final FlightPlanRepository flightPlanRepository;

    // Collected data
    private int totalFlights;
    private int validatedFlights;
    private int completedFlights;
    private int cancelledFlights;
    private double totalDistance;
    private double totalFuel;
    private int violationCount;
    private Map<String, Integer> routeCounts;
    private Map<String, Integer> aircraftCounts;

    public MonthlyReportGenerator(final FlightPlanRepository flightPlanRepository) {
        super(ReportType.MONTHLY);
        this.flightPlanRepository = flightPlanRepository;
    }

    @Override
    protected void collectData(final ReportPeriod period) {
        totalFlights = 0;
        validatedFlights = 0;
        completedFlights = 0;
        cancelledFlights = 0;
        totalDistance = 0.0;
        totalFuel = 0.0;
        routeCounts = new HashMap<>();
        aircraftCounts = new HashMap<>();

        LocalDateTime start = period.startDate().atStartOfDay();
        LocalDateTime end = period.endDate().atTime(23, 59, 59);

        for (FlightPlan fp : flightPlanRepository.findAll()) {
            LocalDateTime dep = fp.departureDateTime();
            if (dep == null || dep.isBefore(start) || dep.isAfter(end)) {
                continue;
            }

            totalFlights++;
            totalDistance += fp.totalDistance();
            totalFuel += fp.estimatedFuel();

            FlightPlanStatus status = fp.status();
            if (status == FlightPlanStatus.VALIDATED || status == FlightPlanStatus.APPROVED
                    || status == FlightPlanStatus.ACTIVE || status == FlightPlanStatus.COMPLETED) {
                validatedFlights++;
            }
            if (status == FlightPlanStatus.COMPLETED) {
                completedFlights++;
            }
            if (status == FlightPlanStatus.CANCELLED) {
                cancelledFlights++;
            }

            // Route aggregation
            String routeKey = fp.departureAirport() + "-" + fp.arrivalAirport();
            routeCounts.merge(routeKey, 1, Integer::sum);

            // Aircraft aggregation
            if (fp.aircraftModel() != null) {
                String acName = fp.aircraftModel().identity().toString();
                aircraftCounts.merge(acName, 1, Integer::sum);
            }
        }
        violationCount = scanSimulationViolations(period);
    }

    @Override
    protected List<String> buildSections(final ReportPeriod period) {
        List<String> sections = new ArrayList<>();
        sections.add(buildSummarySection());
        sections.add(buildFleetSection());
        sections.add(buildSafetySection());
        sections.add(buildRoutesSection());
        return sections;
    }

    private String buildSummarySection() {
        int activeFlights = totalFlights - cancelledFlights;
        double passRate = totalFlights > 0
                ? ((double) (totalFlights - cancelledFlights) / totalFlights) * 100.0
                : 0.0;
        return String.format("SUMMARY: %d flights | %d validated | %d completed | %d cancelled | %.1f%% pass rate",
                totalFlights, validatedFlights, completedFlights, cancelledFlights, passRate);
    }

    private String buildFleetSection() {
        int activeAircraft = aircraftCounts.size();
        String mostUsed = aircraftCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .orElse("N/A");
        return String.format("FLEET: %d active aircraft types | Most used: %s | Total distance: %.1f km",
                activeAircraft, mostUsed, totalDistance);
    }

    private String buildSafetySection() {
        double avgDistance = totalFlights > 0 ? totalDistance / totalFlights : 0.0;
        double avgFuel = totalFlights > 0 ? totalFuel / totalFlights : 0.0;
        return String.format("SAFETY: %.1f km avg distance | %.1f kg avg fuel | %d cancellations | %d violations",
                avgDistance, avgFuel, cancelledFlights, violationCount);
    }

    private int scanSimulationViolations(final ReportPeriod period) {
        Path simDir = Paths.get(SIM_DIR);
        if (!Files.isDirectory(simDir)) return 0;
        String prefix = String.format("simulation_report_%04d%02d", period.year(), period.month());
        int total = 0;
        try (Stream<Path> files = Files.list(simDir)) {
            for (Path file : files.filter(f -> f.getFileName().toString().startsWith(prefix))
                    .filter(f -> f.getFileName().toString().endsWith(".txt")).toList()) {
                boolean inDetails = false;
                for (String line : Files.readAllLines(file)) {
                    if (line.contains("FLIGHT DETAILS")) { inDetails = true; continue; }
                    if (line.contains("SAFETY VIOLATIONS") || line.contains("VALIDATION RESULT")) { inDetails = false; continue; }
                    if (inDetails && line.matches("^\\s*\\S+\\s+\\S+\\s+\\d+\\s+\\d+\\s+.*")) {
                        String[] p = line.trim().split("\\s+");
                        if (p.length >= 4) try { total += Integer.parseInt(p[3]); } catch (NumberFormatException e) {}
                    }
                }
            }
        } catch (IOException e) { return 0; }
        return total;
    }

    int getTotalFlights() { return totalFlights; }
    int getValidatedFlights() { return validatedFlights; }
    int getCompletedFlights() { return completedFlights; }
    int getCancelledFlights() { return cancelledFlights; }
    double getTotalDistance() { return totalDistance; }
    double getTotalFuel() { return totalFuel; }
    int getViolationCount() { return violationCount; }

    private String buildRoutesSection() {
        if (routeCounts.isEmpty()) {
            return "TOP ROUTES: No routes this period";
        }
        StringBuilder sb = new StringBuilder("TOP ROUTES: ");
        routeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> sb.append(String.format("%s(%d) ", e.getKey(), e.getValue())));
        return sb.toString().trim();
    }
}
