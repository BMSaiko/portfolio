package eapli.aisafe.reportmanagement.application;

import eapli.aisafe.reportmanagement.domain.ReportPeriod;
import eapli.aisafe.reportmanagement.domain.ReportType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for report generators using the Template Method pattern.
 * <p>
 * Provides consistent branding, file output, and structure for all report types.
 * Subclasses implement {@link #collectData} and {@link #buildSections}.
 * </p>
 */
public abstract class ReportGenerator {

    private static final String REPORT_DIR = "reports/monthly/";
    private static final int BORDER_WIDTH = 40;
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    protected final ReportType reportType;

    protected ReportGenerator(final ReportType reportType) {
        this.reportType = reportType;
    }

    /**
     * Template method — orchestrates the full report generation flow.
     *
     * @param period the report period
     * @return the path to the generated report file
     * @throws IOException if file writing fails
     */
    public final Path generate(final ReportPeriod period) throws IOException {
        collectData(period);
        List<String> sections = buildSections(period);
        return writeToFile(period, sections);
    }

    /**
     * Collects data for the report. Implemented by subclasses.
     *
     * @param period the report period
     */
    protected abstract void collectData(ReportPeriod period);

    /**
     * Builds the report sections. Implemented by subclasses.
     *
     * @param period the report period
     * @return list of section strings
     */
    protected abstract List<String> buildSections(ReportPeriod period);

    /**
     * Builds the report header with consistent branding.
     *
     * @param period the report period
     * @return header lines
     */
    protected List<String> buildHeader(final ReportPeriod period) {
        List<String> header = new ArrayList<>();
        header.add("=".repeat(BORDER_WIDTH));
        header.add("  AISAFE - " + reportType.name().replace("_", " ") + " REPORT");
        header.add("  Period: " + monthName(period.month()) + " " + period.year()
                + " | Generated: " + LocalDateTime.now().format(TIMESTAMP_FMT));
        header.add("=".repeat(BORDER_WIDTH));
        return header;
    }

    /**
     * Builds the report footer with consistent branding.
     *
     * @return footer lines
     */
    protected List<String> buildFooter() {
        List<String> footer = new ArrayList<>();
        footer.add("=".repeat(BORDER_WIDTH));
        footer.add("  END OF REPORT - AISAFE Flight Control");
        footer.add("=".repeat(BORDER_WIDTH));
        return footer;
    }

    /**
     * Writes the complete report to a file.
     *
     * @param period   the report period
     * @param sections the report sections
     * @return the path to the generated file
     * @throws IOException if writing fails
     */
    protected Path writeToFile(final ReportPeriod period, final List<String> sections) throws IOException {
        Path dir = Paths.get(REPORT_DIR);
        Files.createDirectories(dir);

        String filename = String.format("AISAFE_%sReport_%04d_%02d.txt",
                reportType.name().charAt(0) + reportType.name().substring(1).toLowerCase(),
                period.year(), period.month());
        Path filePath = dir.resolve(filename);

        List<String> lines = new ArrayList<>();
        lines.addAll(buildHeader(period));
        lines.add("");
        int sectionNum = 1;
        for (String section : sections) {
            lines.add(sectionNum + ". " + section);
            lines.add("-".repeat(BORDER_WIDTH));
            sectionNum++;
        }
        lines.add("");
        lines.addAll(buildFooter());

        Files.write(filePath, lines);
        return filePath;
    }

    private String monthName(final int month) {
        return switch (month) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "Unknown";
        };
    }
}
