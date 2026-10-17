package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PhoneNumber implements ValueObject, Comparable<PhoneNumber>, Serializable {

    private String value;

    protected PhoneNumber() {}

    private PhoneNumber(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Phone number is mandatory");
        String cleaned = value.trim();
        if (!cleaned.matches("^[0-9+\\-]{7,15}$"))
            throw new IllegalArgumentException("Phone number must contain 7 to 15 digits and may include + or -");
        this.value = cleaned;
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber)) return false;
        return value.equals(((PhoneNumber) o).value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }

    @Override
    public int compareTo(PhoneNumber other) {
        return this.value.compareTo(other.value);
    }
}
