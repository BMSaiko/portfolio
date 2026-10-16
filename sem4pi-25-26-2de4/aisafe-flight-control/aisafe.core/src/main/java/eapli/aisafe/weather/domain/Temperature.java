package eapli.aisafe.weather.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Temperature implements ValueObject, Comparable<Temperature>, Serializable {

    private double value;

    protected Temperature() {}

    private Temperature(double value) {
        if (value < -50 || value > 60)
            throw new IllegalArgumentException("Temperature must be between -50 and 60 ºC");
        this.value = value;
    }

    public static Temperature of(double value) {
        return new Temperature(value);
    }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Temperature)) return false;
        return Double.compare(value, ((Temperature) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.valueOf(value); }

    @Override
    public int compareTo(Temperature other) {
        return Double.compare(this.value, other.value);
    }
}
