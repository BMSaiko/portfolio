package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Weight implements ValueObject, Comparable<Weight>, Serializable {

    private double value;

    protected Weight() {}

    private Weight(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Weight must be positive.");
        this.value = value;
    }

    public static Weight of(double value) { return new Weight(value); }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weight)) return false;
        return Double.compare(value, ((Weight) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.format("%.2f kg", value); }

    @Override
    public int compareTo(Weight other) { return Double.compare(this.value, other.value); }
}
