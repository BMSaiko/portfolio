package eapli.aisafe.routemanagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing a flight route designator.
 *
 * <p>Format: exactly 2 uppercase letters (the company's IATA code)
 * followed by 1–4 digits — e.g. {@code TP123}.</p>
 *
 * <p>Supports US073 — Create a flight route.</p>
 */
@Embeddable
public class RouteDesignator implements Comparable<RouteDesignator> {

    private static final Pattern FORMAT = Pattern.compile("^[A-Z]{2}[0-9]{1,4}$");

    @Column(name = "DESIGNATOR", nullable = false, unique = true, length = 6)
    private String value;

    /** For ORM only. */
    protected RouteDesignator() {
    }

    /**
     * Creates a route designator after validating format and IATA prefix.
     *
     * @param rawDesignator   the designator string (e.g. {@code "TP123"})
     * @param companyIataCode the 2-letter IATA code of the owning company
     * @throws IllegalArgumentException if the format is invalid or the prefix
     *                                  does not match the company's IATA code
     */
    public RouteDesignator(final String rawDesignator, final String companyIataCode) {
        if (rawDesignator == null || rawDesignator.isBlank()) {
            throw new IllegalArgumentException("Route designator cannot be blank.");
        }
        if (companyIataCode == null || companyIataCode.isBlank()) {
            throw new IllegalArgumentException("Company IATA code cannot be blank.");
        }

        final String normalized = rawDesignator.trim().toUpperCase();

        if (!FORMAT.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Route designator must be 2 letters followed by 1–4 digits (e.g. TP123). Got: "
                            + normalized);
        }

        final String prefix = normalized.substring(0, 2);
        if (!prefix.equalsIgnoreCase(companyIataCode.trim())) {
            throw new IllegalArgumentException(
                    "Route designator prefix '" + prefix
                            + "' does not match the company IATA code '"
                            + companyIataCode.trim().toUpperCase() + "'.");
        }

        this.value = normalized;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteDesignator)) return false;
        return value.equals(((RouteDesignator) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(final RouteDesignator other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}