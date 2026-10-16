package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Thrust implements ValueObject, Comparable<Thrust>, Serializable {

    private double value;

    protected Thrust() {}

    private Thrust(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Thrust must be positive.");
        this.value = value;
    }

    public static Thrust of(double value) { return new Thrust(value); }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Thrust)) return false;
        return Double.compare(value, ((Thrust) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.format("%.0f N", value); }

    @Override
    public int compareTo(Thrust other) { return Double.compare(this.value, other.value); }
}
