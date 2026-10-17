package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.airportmanagement.repositories.AirportRepository;

import java.util.*;

public class InMemoryAirportRepository implements AirportRepository {

    private final Map<String, Airport> store = new LinkedHashMap<>();

    public void add(Airport airport) {
        store.put(airport.identity().value(), airport);
    }

    @Override
    public Optional<Airport> findByIataCode(IataCode iataCode) {
        return Optional.ofNullable(store.get(iataCode.value()));
    }

    @Override
    public Optional<Airport> findByIcaoCode(IcaoCode icaoCode) {
        return store.values().stream()
                .filter(a -> a.icaoCode().equals(icaoCode))
                .findFirst();
    }

    @Override
    public Iterable<Airport> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Airport save(Airport airport) {
        store.put(airport.identity().value(), airport);
        return airport;
    }

    public void delete(Airport entity) {
        store.remove(entity.identity().value());
    }

    public Optional<Airport> findById(IataCode iataCode) {
        return findByIataCode(iataCode);
    }

    public long count() {
        return store.size();
    }
}
