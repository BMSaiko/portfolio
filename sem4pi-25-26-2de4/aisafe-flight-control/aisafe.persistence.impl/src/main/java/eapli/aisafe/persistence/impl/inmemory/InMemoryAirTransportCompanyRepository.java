package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;

import java.util.*;

public class InMemoryAirTransportCompanyRepository implements AirTransportCompanyRepository {

    private final Map<Long, AirTransportCompany> store = new LinkedHashMap<>();
    private long nextId = 1;

    public void add(AirTransportCompany company) {
        store.put(nextId++, company);
    }

    @Override
    public Optional<AirTransportCompany> findByIataCode(String iataCode) {
        return store.values().stream()
                .filter(c -> c.iataCode().equalsIgnoreCase(iataCode))
                .findFirst();
    }

    @Override
    public Optional<AirTransportCompany> findByIcaoCode(String icaoCode) {
        return store.values().stream()
                .filter(c -> c.icaoCode().equalsIgnoreCase(icaoCode))
                .findFirst();
    }

    @Override
    public boolean existsByIataCode(String iataCode) {
        return store.values().stream().anyMatch(c -> c.iataCode().equalsIgnoreCase(iataCode));
    }

    @Override
    public boolean existsByIcaoCode(String icaoCode) {
        return store.values().stream().anyMatch(c -> c.icaoCode().equalsIgnoreCase(icaoCode));
    }

    @Override
    public Iterable<AirTransportCompany> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public AirTransportCompany save(AirTransportCompany entity) {
        store.put(nextId++, entity);
        return entity;
    }

    @Override
    public void delete(AirTransportCompany entity) {
        store.values().removeIf(c -> c.hashCode() == entity.hashCode());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public Optional<AirTransportCompany> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public long count() {
        return store.size();
    }
}
