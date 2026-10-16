package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value object that represents a closed time interval with a start and an end
 * date-time.
 *
 * <p>
 * A {@code TimePeriod} is used whenever the domain needs to express that a
 * certain piece of information is valid during a specific interval. For example,
 * weather data uses this value object to define the period during which a
 * weather record applies.
 * </p>
 *
 * <p>
 * The period is valid only when both date-time values are present and the end
 * is strictly after the start. Periods with equal start and end timestamps are
 * not allowed.
 * </p>
 */
@Embeddable
public class TimePeriod implements ValueObject, Comparable<TimePeriod>, Serializable {

    /**
     * Start date and time of the period.
     */
    private LocalDateTime start;

    /**
     * End date and time of the period.
     */
    private LocalDateTime end;

    /**
     * Protected constructor required by JPA.
     *
     * <p>
     * Application code should use {@link #of(LocalDateTime, LocalDateTime)}
     * to create valid instances.
     * </p>
     */
    protected TimePeriod() {
    }

    /**
     * Creates a time period.
     *
     * @param start the start date and time of the period
     * @param end the end date and time of the period
     * @throws IllegalArgumentException if {@code start} is {@code null}
     * @throws IllegalArgumentException if {@code end} is {@code null}
     * @throws IllegalArgumentException if {@code end} is not strictly after
     *                                  {@code start}
     */
    private TimePeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            throw new IllegalArgumentException("Start date/time is required");
        }
        if (end == null) {
            throw new IllegalArgumentException("End date/time is required");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End date/time must be after start date/time");
        }

        this.start = start;
        this.end = end;
    }

    /**
     * Factory method for creating a valid time period.
     *
     * @param start the start date and time of the period
     * @param end the end date and time of the period
     * @return a new {@code TimePeriod}
     * @throws IllegalArgumentException if any invariant of the period is
     *                                  violated
     */
    public static TimePeriod of(LocalDateTime start, LocalDateTime end) {
        return new TimePeriod(start, end);
    }

    /**
     * Returns the start date and time of this period.
     *
     * @return the start date and time
     */
    public LocalDateTime start() {
        return start;
    }

    /**
     * Returns the end date and time of this period.
     *
     * @return the end date and time
     */
    public LocalDateTime end() {
        return end;
    }

    /**
     * Compares this period with another period using the start date-time.
     *
     * <p>
     * This ordering is useful when time periods need to be sorted
     * chronologically by their beginning.
     * </p>
     *
     * @param other the other time period to compare with
     * @return a negative value, zero, or a positive value if this period starts
     * before, at the same time as, or after the other period
     */
    @Override
    public int compareTo(TimePeriod other) {
        return this.start.compareTo(other.start);
    }

    /**
     * Checks whether this time period is equal to another object.
     *
     * <p>
     * Two periods are equal when both their start and end date-time values are
     * equal.
     * </p>
     *
     * @param o the object to compare with this time period
     * @return {@code true} if the given object represents the same period;
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimePeriod)) return false;
        TimePeriod that = (TimePeriod) o;
        return Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    /**
     * Returns the hash code for this time period.
     *
     * @return hash code based on start and end date-time values
     */
    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    /**
     * Returns a textual representation of the time period.
     *
     * @return a string containing the start and end date-time values
     */
    @Override
    public String toString() {
        return start + " to " + end;
    }
}