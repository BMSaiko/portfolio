package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Area implements ValueObject, Comparable<Area>, Serializable {

    private double value;

    protected Area() {}

    private Area(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("Area must be positive.");
        this.value = value;
    }

    public static Area of(double value) { return new Area(value); }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Area)) return false;
        return Double.compare(value, ((Area) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.format("%.2f m²", value); }

    @Override
    public int compareTo(Area other) { return Double.compare(this.value, other.value); }
}
