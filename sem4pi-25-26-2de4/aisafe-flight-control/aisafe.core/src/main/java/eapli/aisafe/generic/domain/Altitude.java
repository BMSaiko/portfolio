package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Altitude implements ValueObject, Comparable<Altitude>, Serializable {

    private double value;

    protected Altitude() {}

    private Altitude(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Altitude must be positive.");
        this.value = value;
    }

    public static Altitude of(double value) { return new Altitude(value); }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Altitude)) return false;
        return Double.compare(value, ((Altitude) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.format("%.0f m", value); }

    @Override
    public int compareTo(Altitude other) { return Double.compare(this.value, other.value); }
}
