package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Coefficient implements ValueObject, Comparable<Coefficient>, Serializable {

    private double value;

    protected Coefficient() {}

    private Coefficient(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Coefficient must be positive.");
        this.value = value;
    }

    public static Coefficient of(double value) { return new Coefficient(value); }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coefficient)) return false;
        return Double.compare(value, ((Coefficient) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.format("%.4f", value); }

    @Override
    public int compareTo(Coefficient other) { return Double.compare(this.value, other.value); }
}
