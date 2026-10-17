package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TSFC implements ValueObject, Comparable<TSFC>, Serializable {

    private double value;

    protected TSFC() {}

    private TSFC(double value) {
        if (value <= 0)
            throw new IllegalArgumentException("TSFC must be positive.");
        this.value = value;
    }

    public static TSFC of(double value) { return new TSFC(value); }

    public double value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TSFC)) return false;
        return Double.compare(value, ((TSFC) o).value) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return String.format("%.2e", value); }

    @Override
    public int compareTo(TSFC other) { return Double.compare(this.value, other.value); }
}
