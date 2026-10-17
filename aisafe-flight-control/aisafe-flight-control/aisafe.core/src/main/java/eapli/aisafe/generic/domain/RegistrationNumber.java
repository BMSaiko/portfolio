package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RegistrationNumber implements ValueObject, Comparable<RegistrationNumber>, Serializable {

    private String value;

    protected RegistrationNumber() {}

    private RegistrationNumber(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Registration number is mandatory.");
        if (!value.matches("[A-Z0-9-]{2,10}"))
            throw new IllegalArgumentException("Registration number must be 2-10 characters: uppercase letters, digits, or hyphens.");
        this.value = value;
    }

    public static RegistrationNumber of(String value) {
        if (value == null) throw new IllegalArgumentException("Registration number must not be null.");
        return new RegistrationNumber(value.trim().toUpperCase());
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistrationNumber)) return false;
        return value.equals(((RegistrationNumber) o).value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }

    @Override
    public int compareTo(RegistrationNumber other) { return this.value.compareTo(other.value); }
}
