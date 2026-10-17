package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryFlightRouteRepository implements FlightRouteRepository {

    private final Map<String, FlightRoute> store = new LinkedHashMap<>();

    @Override
    public Optional<FlightRoute> findByDesignator(final String designatorValue) {
        return Optional.ofNullable(store.get(designatorValue.trim().toUpperCase()));
    }

    @Override
    public boolean existsByDesignator(final String designatorValue) {
        return store.containsKey(designatorValue.trim().toUpperCase());
    }

    @Override
    public List<FlightRoute> findActiveByCompany(final AirTransportCompany company) {
        return store.values().stream()
                .filter(r -> r.company().equals(company) && r.isActive())
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightRoute> findAllByCompany(final AirTransportCompany company) {
        return store.values().stream()
                .filter(r -> r.company().equals(company))
                .collect(Collectors.toList());
    }

    @Override
    public FlightRoute save(final FlightRoute route) {
        store.put(route.identity().value(), route);
        return route;
    }

    @Override
    public Iterable<FlightRoute> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void delete(final FlightRoute route) {
        store.remove(route.identity().value());
    }

    public long count() {
        return store.size();
    }
}