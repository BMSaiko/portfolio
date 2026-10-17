package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;

import java.util.*;

public class InMemoryAirControlAreaRepository implements AirControlAreaRepository {

    private final Map<AirControlAreaCode, AirControlArea> store = new LinkedHashMap<>();

    public void add(AirControlArea area) {
        store.put(area.identity(), area);
    }

    @Override
    public Optional<AirControlArea> findByCode(AirControlAreaCode code) {
        return Optional.ofNullable(store.get(code));
    }

    @Override
    public Iterable<AirControlArea> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public AirControlArea save(AirControlArea entity) {
        store.put(entity.identity(), entity);
        return entity;
    }

    @Override
    public Optional<AirControlArea> findById(AirControlAreaCode code) {
        return findByCode(code);
    }

    @Override
    public void delete(AirControlArea entity) {
        store.remove(entity.identity());
    }

    @Override
    public void deleteById(AirControlAreaCode code) {
        store.remove(code);
    }

    @Override
    public long count() {
        return store.size();
    }

    public Optional<AirControlArea> ofIdentity(AirControlAreaCode code) {
        return findById(code);
    }

    public Iterable<AirControlArea> all() {
        return findAll();
    }
}
