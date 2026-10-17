package eapli.aisafe.weather.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WindSpeed implements ValueObject, Comparable<WindSpeed>, Serializable {

    private double value;

    protected WindSpeed() {}

    private WindSpeed(double value) {
        if (value < 0)
            throw new IllegalArgumentException("Wind speed cannot be negative");
        this.value = value;
    }

    public static WindSpeed of(double value) {
        return new WindSpeed(value);
    }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WindSpeed)) return false;
        return Double.compare(value, ((WindSpeed) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.valueOf(value); }

    @Override
    public int compareTo(WindSpeed other) {
        return Double.compare(this.value, other.value);
    }
}
