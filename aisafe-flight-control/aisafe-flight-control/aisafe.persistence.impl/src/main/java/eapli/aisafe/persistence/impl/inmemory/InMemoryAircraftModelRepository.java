package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;

import java.util.*;

public class InMemoryAircraftModelRepository implements AircraftModelRepository {

    private final Map<AircraftModelId, AircraftModel> store = new LinkedHashMap<>();

    public void add(AircraftModel model) {
        store.put(model.identity(), model);
    }

    @Override
    public Optional<AircraftModel> findById(AircraftModelId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Iterable<AircraftModel> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public AircraftModel save(AircraftModel aircraftModel) {
        store.put(aircraftModel.identity(), aircraftModel);
        return aircraftModel;
    }

    @Override
    public Iterable<String> findAllMakers() {
        Set<String> makers = new LinkedHashSet<>();
        store.values().forEach(m -> makers.add(m.makerName()));
        return makers;
    }

    @Override
    public Iterable<AircraftModel> findByMaker(String makerName) {
        return store.values().stream()
                .filter(m -> m.makerName().equalsIgnoreCase(makerName))
                .toList();
    }
}
