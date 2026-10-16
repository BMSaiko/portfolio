package eapli.aisafe.generic.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IcaoCode implements Serializable {

    private String icaoCode;

    protected IcaoCode() {}

    private IcaoCode(final String code) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("ICAO code must not be empty.");
        if (!code.matches("[A-Z]{4}"))
            throw new IllegalArgumentException(
                    "ICAO code must be exactly 4 uppercase letters (e.g. LPPT).");
        this.icaoCode = code;
    }

    public static IcaoCode of(final String code) {
        if (code == null) throw new IllegalArgumentException("ICAO code must not be null.");
        return new IcaoCode(code.toUpperCase().trim());
    }

    public String value() { return icaoCode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IcaoCode)) return false;
        return icaoCode.equals(((IcaoCode) o).icaoCode);
    }

    @Override
    public int hashCode() { return Objects.hash(icaoCode); }

    @Override
    public String toString() { return icaoCode; }
}
