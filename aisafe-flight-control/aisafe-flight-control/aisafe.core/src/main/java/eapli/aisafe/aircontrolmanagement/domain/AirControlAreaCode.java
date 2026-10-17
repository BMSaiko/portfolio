package eapli.aisafe.aircontrolmanagement.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object that represents the unique code of an air control area.
 *
 * <p>
 * The code is used as the business identity of {@link AirControlArea}. It is
 * embedded in the air control area aggregate and is also used by other parts of
 * the system, such as weather data registration, weather data consultation and
 * import operations.
 * </p>
 *
 * <p>
 * Valid codes must contain between 2 and 10 characters and may include only
 * uppercase alphanumeric characters, underscores and hyphens. The value is
 * normalized to uppercase when created.
 * </p>
 */
@Embeddable
public class AirControlAreaCode implements ValueObject, Comparable<AirControlAreaCode>, Serializable {

    /**
     * Normalized air control area code.
     */
    private String code;

    /**
     * Protected constructor required by JPA.
     *
     * <p>
     * Application code should use {@link #of(String)} to create valid
     * instances.
     * </p>
     */
    protected AirControlAreaCode() {}

    /**
     * Creates an air control area code.
     *
     * @param code textual code value
     * @throws IllegalArgumentException if {@code code} is {@code null} or blank
     * @throws IllegalArgumentException if {@code code} does not match the
     *                                  required format
     */
    private AirControlAreaCode(final String code) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Area code must not be empty.");
        if (!code.matches("[A-Z0-9_\\-]{2,10}"))
            throw new IllegalArgumentException(
                    "Area code must be 2-10 uppercase alphanumeric characters.");
        this.code = code.toUpperCase();
    }

    /**
     * Factory method for creating an air control area code.
     *
     * @param code textual code value
     * @return a valid air control area code
     * @throws IllegalArgumentException if the provided value is invalid
     */
    public static AirControlAreaCode of(final String code) {
        return new AirControlAreaCode(code);
    }

    /**
     * Returns the normalized code value.
     *
     * @return air control area code
     */
    public String value() { return code; }

    /**
     * Checks equality by comparing the normalized code value.
     *
     * @param o object to compare with this code
     * @return {@code true} if both objects represent the same code; otherwise
     * {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AirControlAreaCode)) return false;
        return code.equals(((AirControlAreaCode) o).code);
    }

    /**
     * Returns the hash code based on the normalized code value.
     *
     * @return hash code based on the code
     */
    @Override
    public int hashCode() { return Objects.hash(code); }

    /**
     * Returns the normalized code as text.
     *
     * @return code text
     */
    @Override
    public String toString() { return code; }

    /**
     * Compares this code with another air control area code lexicographically.
     *
     * @param other the other code to compare with
     * @return a negative value, zero, or a positive value depending on the
     * lexicographical order of the codes
     */
    @Override
    public int compareTo(AirControlAreaCode other) {
        return this.code.compareTo(other.code);
    }
}