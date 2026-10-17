package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Speed implements ValueObject, Comparable<Speed>, Serializable {

    private double value;

    protected Speed() {}

    private Speed(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Speed must be positive.");
        this.value = value;
    }

    public static Speed of(double value) { return new Speed(value); }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Speed)) return false;
        return Double.compare(value, ((Speed) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.format("%.4f Mach", value); }

    @Override
    public int compareTo(Speed other) { return Double.compare(this.value, other.value); }
}
