package eapli.aisafe.reportmanagement.application;

import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.reportmanagement.domain.MonthlyStatistics;
import eapli.aisafe.reportmanagement.domain.ReportPeriod;
import eapli.aisafe.reportmanagement.repositories.MonthlyStatisticsRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Application service for monthly report generation.
 * <p>
 * Handles authorization, delegates to MonthlyReportGenerator,
 * and optionally persists MonthlyStatistics for caching.
 * </p>
 */
public class MonthlyReportService {

    private final AuthService authService;
    private final MonthlyReportGenerator reportGenerator;
    private final MonthlyStatisticsRepository statisticsRepository;

    public MonthlyReportService(final AuthService authService,
                                 final FlightPlanRepository flightPlanRepository,
                                 final MonthlyStatisticsRepository statisticsRepository) {
        this.authService = authService;
        this.reportGenerator = new MonthlyReportGenerator(flightPlanRepository);
        this.statisticsRepository = statisticsRepository;
    }

    /**
     * Generates a monthly report for the given period.
     * <p>
     * Requires ADMIN or FLIGHT_CONTROL_OPERATOR role.
     * </p>
     *
     * @param period the report period
     * @return the path to the generated report file
     * @throws IOException              if file writing fails
     * @throws SecurityException        if user lacks required role
     * @throws IllegalArgumentException if period is invalid
     */
    public Path generateMonthlyReport(final ReportPeriod period) throws IOException {
        authService.ensureHasAnyRole(Role.ADMIN, Role.FLIGHT_CONTROL_OPERATOR);

        Path reportPath = reportGenerator.generate(period);

        // Persist statistics for caching
        persistStatistics(period);

        return reportPath;
    }

    private void persistStatistics(final ReportPeriod period) {
        if (statisticsRepository.findByYearAndMonth(period.year(), period.month()).isPresent()) {
            return;
        }
        MonthlyStatistics stats = new MonthlyStatistics(
                period.year(), period.month(),
                reportGenerator.getTotalFlights(),
                reportGenerator.getValidatedFlights(),
                reportGenerator.getCompletedFlights(),
                reportGenerator.getCancelledFlights(),
                reportGenerator.getTotalDistance(),
                reportGenerator.getTotalFuel(),
                LocalDateTime.now());
        statisticsRepository.save(stats);
    }
}
