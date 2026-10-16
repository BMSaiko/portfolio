package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IataCode implements Serializable {

    private String iataCode;

    protected IataCode() {}

    private IataCode(final String code) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("IATA code must not be empty.");
        if (!code.matches("[A-Z]{3}"))
            throw new IllegalArgumentException(
                    "IATA code must be exactly 3 uppercase letters (e.g. LIS).");
        this.iataCode = code;
    }

    public static IataCode of(final String code) {
        if (code == null) throw new IllegalArgumentException("IATA code must not be null.");
        return new IataCode(code.toUpperCase().trim());
    }

    public String value() { return iataCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IataCode)) return false;
        return iataCode.equals(((IataCode) o).iataCode);
    }

    @Override
    public int hashCode() { return Objects.hash(iataCode); }

    @Override
    public String toString() { return iataCode; }
}