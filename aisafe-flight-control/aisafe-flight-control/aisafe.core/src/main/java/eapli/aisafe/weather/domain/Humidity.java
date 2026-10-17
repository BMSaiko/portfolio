package eapli.aisafe.weather.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Humidity implements ValueObject, Comparable<Humidity>, Serializable {

    private double value;

    protected Humidity() {}

    private Humidity(double value) {
        if (value < 0 || value > 100)
            throw new IllegalArgumentException("Humidity must be between 0 and 100 %");
        this.value = value;
    }

    public static Humidity of(double value) {
        return new Humidity(value);
    }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Humidity)) return false;
        return Double.compare(value, ((Humidity) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.valueOf(value); }

    @Override
    public int compareTo(Humidity other) {
        return Double.compare(this.value, other.value);
    }
}
