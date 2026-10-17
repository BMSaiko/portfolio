package eapli.aisafe.weather.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Pressure implements ValueObject, Comparable<Pressure>, Serializable {

    private double value;

    protected Pressure() {}

    private Pressure(double value) {
        if (value < 800 || value > 1100)
            throw new IllegalArgumentException("Pressure must be between 800 and 1100 hPa");
        this.value = value;
    }

    public static Pressure of(double value) {
        return new Pressure(value);
    }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pressure)) return false;
        return Double.compare(value, ((Pressure) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.valueOf(value); }

    @Override
    public int compareTo(Pressure other) {
        return Double.compare(this.value, other.value);
    }
}
