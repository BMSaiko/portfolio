package eapli.aisafe.reportmanagement.domain;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Value Object representing a report period (year + month).
 * <p>
 * Immutable, validates month range [1-12] and year >= 2000.
 * </p>
 */
public final class ReportPeriod {

    private final int year;
    private final int month;

    private ReportPeriod(final int year, final int month) {
        this.year = year;
        this.month = month;
    }

    /**
     * Factory method for creating a ReportPeriod.
     *
     * @param year  the year (must be >= 2000)
     * @param month the month (1-12)
     * @return a new ReportPeriod instance
     * @throws IllegalArgumentException if parameters are invalid
     */
    public static ReportPeriod of(final int year, final int month) {
        if (year < 2000) {
            throw new IllegalArgumentException("Year must be >= 2000.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }
        return new ReportPeriod(year, month);
    }

    public int year() {
        return year;
    }

    public int month() {
        return month;
    }

    /**
     * Returns the first day of this period.
     */
    public LocalDate startDate() {
        return LocalDate.of(year, month, 1);
    }

    /**
     * Returns the last day of this period.
     */
    public LocalDate endDate() {
        return YearMonth.of(year, month).atEndOfMonth();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportPeriod)) return false;
        ReportPeriod that = (ReportPeriod) o;
        return year == that.year && month == that.month;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month);
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d", year, month);
    }
}
