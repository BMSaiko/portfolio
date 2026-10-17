package eapli.aisafe.airportmanagement.repositories;

import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;

import java.util.Optional;

public interface AirportRepository {

    Optional<Airport> findByIataCode(IataCode iataCode);

    Optional<Airport> findByIcaoCode(IcaoCode icaoCode);

    Iterable<Airport> findAll();

    Airport save(Airport airport);
}
