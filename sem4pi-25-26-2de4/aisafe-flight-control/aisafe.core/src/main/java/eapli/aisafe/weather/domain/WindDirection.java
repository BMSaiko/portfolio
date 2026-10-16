package eapli.aisafe.weather.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WindDirection implements ValueObject, Comparable<WindDirection>, Serializable {

    private double value;

    protected WindDirection() {}

    private WindDirection(double value) {
        if (value < 0 || value > 360)
            throw new IllegalArgumentException("Wind direction must be between 0 and 360 degrees");
        this.value = value;
    }

    public static WindDirection of(double value) {
        return new WindDirection(value);
    }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WindDirection)) return false;
        return Double.compare(value, ((WindDirection) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.valueOf(value); }

    @Override
    public int compareTo(WindDirection other) {
        return Double.compare(this.value, other.value);
    }
}
