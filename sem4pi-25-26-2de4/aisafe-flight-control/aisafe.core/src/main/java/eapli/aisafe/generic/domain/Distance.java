package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Distance implements ValueObject, Comparable<Distance>, Serializable {

    private double value;

    protected Distance() {}

    private Distance(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Distance must be positive.");
        this.value = value;
    }

    public static Distance of(double value) { return new Distance(value); }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Distance)) return false;
        return Double.compare(value, ((Distance) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.format("%.0f km", value); }

    @Override
    public int compareTo(Distance other) { return Double.compare(this.value, other.value); }
}
