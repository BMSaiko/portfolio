package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;

import java.util.*;

public class InMemoryEngineModelRepository implements EngineModelRepository {

    private final Map<EngineModelId, EngineModel> store = new LinkedHashMap<>();

    public void add(EngineModel model) {
        store.put(model.identity(), model);
    }

    @Override
    public Optional<EngineModel> findById(EngineModelId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Iterable<EngineModel> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Iterable<EngineModel> findByType(EngineType type) {
        return store.values().stream()
                .filter(e -> e.engineType() == type)
                .toList();
    }

    @Override
    public EngineModel save(EngineModel engineModel) {
        store.put(engineModel.identity(), engineModel);
        return engineModel;
    }
}
