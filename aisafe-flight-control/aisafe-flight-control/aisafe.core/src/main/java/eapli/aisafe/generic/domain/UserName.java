package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserName implements ValueObject, Comparable<UserName>, Serializable {

    private String value;

    protected UserName() {}

    private UserName(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("User name is mandatory");
        if (value.trim().length() < 2 || value.trim().length() > 80)
            throw new IllegalArgumentException("User name must be between 2 and 80 characters");
        this.value = value.trim();
    }

    public static UserName of(String value) {
        return new UserName(value);
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserName)) return false;
        return value.equals(((UserName) o).value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }

    @Override
    public int compareTo(UserName other) {
        return this.value.compareTo(other.value);
    }
}
