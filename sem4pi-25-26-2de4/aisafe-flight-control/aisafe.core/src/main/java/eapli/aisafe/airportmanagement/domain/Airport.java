package eapli.aisafe.airportmanagement.domain;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "AIRPORT")
public class Airport {

    @EmbeddedId
    private IataCode iataCode;

    @Embedded
    @AttributeOverride(name = "icaoCode", column = @Column(name = "icao_code", unique = true, nullable = false))
    private IcaoCode icaoCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String town;

    @Column(nullable = false)
    private String country;

    @Embedded
    private Coordinates coordinates;

    @ManyToOne(optional = false)
    @JoinColumn(name = "area_code", nullable = false)
    private AirControlArea airControlArea;

    protected Airport() {}

    public Airport(final IataCode iataCode,
                   final IcaoCode icaoCode,
                   final String name,
                   final String town,
                   final String country,
                   final Coordinates coordinates,
                   final AirControlArea airControlArea) {
        if (iataCode == null)
            throw new IllegalArgumentException("IATA code must not be null.");
        if (icaoCode == null)
            throw new IllegalArgumentException("ICAO code must not be null.");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be empty.");
        if (town == null || town.isBlank())
            throw new IllegalArgumentException("Town must not be empty.");
        if (country == null || country.isBlank())
            throw new IllegalArgumentException("Country must not be empty.");
        if (coordinates == null)
            throw new IllegalArgumentException("Coordinates must not be null.");
        if (airControlArea == null)
            throw new IllegalArgumentException("Air Control Area must not be null.");
        this.iataCode = iataCode;
        this.icaoCode = icaoCode;
        this.name = name;
        this.town = town;
        this.country = country;
        this.coordinates = coordinates;
        this.airControlArea = airControlArea;
    }

    public IataCode identity() { return iataCode; }
    public IcaoCode icaoCode() { return icaoCode; }
    public String name() { return name; }
    public String town() { return town; }
    public String country() { return country; }
    public Coordinates coordinates() { return coordinates; }
    public AirControlArea airControlArea() { return airControlArea; }

    public boolean sameAs(Object other) {
        if (!(other instanceof Airport)) return false;
        return this.iataCode.equals(((Airport) other).iataCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airport)) return false;
        return iataCode.equals(((Airport) o).iataCode);
    }

    @Override
    public int hashCode() { return Objects.hash(iataCode); }

    @Override
    public String toString() {
        return String.format("[%s/%s] %s, %s, %s | %s | Area: %s",
                iataCode, icaoCode, name, town, country,
                coordinates, airControlArea.identity());
    }
}
