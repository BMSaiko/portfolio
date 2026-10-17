package eapli.aisafe.app.backoffice.console.presentation.reportmanagement;

import eapli.aisafe.reportmanagement.application.MonthlyReportService;
import eapli.aisafe.reportmanagement.domain.ReportPeriod;
import eapli.aisafe.app.common.console.AbstractUI;

import java.io.IOException;
import java.nio.file.Path;

/**
 * UI for generating monthly reports.
 * <p>
 * Prompts the user for month and year, then delegates to
 * MonthlyReportService to generate the report.
 * </p>
 */
public class GenerateMonthlyReportUI extends AbstractUI {

    private final MonthlyReportService monthlyReportService;

    public GenerateMonthlyReportUI(final MonthlyReportService monthlyReportService) {
        this.monthlyReportService = monthlyReportService;
    }

    @Override
    protected boolean doShow() {
        System.out.println();
        System.out.println("=== Generate Monthly Report ===");

        int year = readYear();
        int month = readMonth();

        try {
            ReportPeriod period = ReportPeriod.of(year, month);
            Path reportPath = monthlyReportService.generateMonthlyReport(period);
            System.out.println("Report generated successfully: " + reportPath.toAbsolutePath());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Access denied: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Failed to write report: " + e.getMessage());
        }

        return true;
    }

    private int readYear() {
        while (true) {
            System.out.print("Year (>= 2000): ");
            String input = scanner.nextLine().trim();
            try {
                int year = Integer.parseInt(input);
                if (year >= 2000) return year;
                System.out.println("Year must be >= 2000.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private int readMonth() {
        while (true) {
            System.out.print("Month (1-12): ");
            String input = scanner.nextLine().trim();
            try {
                int month = Integer.parseInt(input);
                if (month >= 1 && month <= 12) return month;
                System.out.println("Month must be between 1 and 12.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }
}
