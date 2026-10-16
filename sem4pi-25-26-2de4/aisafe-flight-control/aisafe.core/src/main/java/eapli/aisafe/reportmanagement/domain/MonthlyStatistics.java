package eapli.aisafe.reportmanagement.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA entity representing pre-computed monthly statistics for reports.
 * <p>
 * Stores aggregated metrics for a specific month/year combination,
 * enabling fast report regeneration without re-scanning flight plans.
 * </p>
 */
@Entity
@Table(name = "MONTHLY_STATISTICS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"year_value", "month_value"})
})
public class MonthlyStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_value", nullable = false)
    private int year;

    @Column(name = "month_value", nullable = false)
    private int month;

    @Column(name = "total_flights", nullable = false)
    private int totalFlights;

    @Column(name = "validated_flights", nullable = false)
    private int validatedFlights;

    @Column(name = "completed_flights", nullable = false)
    private int completedFlights;

    @Column(name = "cancelled_flights", nullable = false)
    private int cancelledFlights;

    @Column(name = "total_distance", nullable = false)
    private double totalDistance;

    @Column(name = "total_fuel", nullable = false)
    private double totalFuel;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    protected MonthlyStatistics() {
        // for ORM
    }

    public MonthlyStatistics(final int year,
                              final int month,
                              final int totalFlights,
                              final int validatedFlights,
                              final int completedFlights,
                              final int cancelledFlights,
                              final double totalDistance,
                              final double totalFuel,
                              final LocalDateTime generatedAt) {
        if (year < 2000) throw new IllegalArgumentException("Year must be >= 2000.");
        if (month < 1 || month > 12) throw new IllegalArgumentException("Month must be 1-12.");
        if (generatedAt == null) throw new IllegalArgumentException("GeneratedAt must not be null.");

        this.year = year;
        this.month = month;
        this.totalFlights = totalFlights;
        this.validatedFlights = validatedFlights;
        this.completedFlights = completedFlights;
        this.cancelledFlights = cancelledFlights;
        this.totalDistance = totalDistance;
        this.totalFuel = totalFuel;
        this.generatedAt = generatedAt;
    }

    public int year() { return year; }
    public int month() { return month; }
    public int totalFlights() { return totalFlights; }
    public int validatedFlights() { return validatedFlights; }
    public int completedFlights() { return completedFlights; }
    public int cancelledFlights() { return cancelledFlights; }
    public double totalDistance() { return totalDistance; }
    public double totalFuel() { return totalFuel; }
    public LocalDateTime generatedAt() { return generatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MonthlyStatistics)) return false;
        MonthlyStatistics that = (MonthlyStatistics) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
