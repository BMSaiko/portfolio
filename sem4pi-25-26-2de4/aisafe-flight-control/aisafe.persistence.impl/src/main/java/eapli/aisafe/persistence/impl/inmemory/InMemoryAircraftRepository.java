package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;

import java.util.*;

public class InMemoryAircraftRepository implements AircraftRepository {

    private final Map<String, Aircraft> store = new HashMap<>();

    public void add(Aircraft aircraft) {
        store.put(aircraft.identity(), aircraft);
    }

    @Override
    public boolean existsByRegistration(String registration) {
        return store.containsKey(registration);
    }

    @Override
    public boolean existsByAircraftModelId(AircraftModelId id) {
        return store.values().stream().anyMatch(a -> a.model().identity().equals(id));
    }

    @Override
    public Iterable<Aircraft> findByCompany(String companyIataCode) {
        return store.values().stream()
                .filter(a -> a.company().iataCode().equalsIgnoreCase(companyIataCode))
                .toList();
    }

    @Override
    public Iterable<Aircraft> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Aircraft save(Aircraft entity) {
        store.put(entity.identity(), entity);
        return entity;
    }

    @Override
    public void delete(Aircraft entity) {
        store.remove(entity.identity());
    }

    public Optional<Aircraft> findById(String registration) {
        return Optional.ofNullable(store.get(registration));
    }

    @Override
    public Optional<Aircraft> findByRegistration(String registration) {
        return Optional.ofNullable(store.get(registration));
    }

    public Optional<Aircraft> ofIdentity(String id) {
        return findById(id);
    }

    public void deleteOfIdentity(String id) {
        store.remove(id);
    }

    @Override
    public long count() {
        return store.size();
    }
}
