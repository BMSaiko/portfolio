package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.usermanagement.domain.Email;

import java.util.*;

public class InMemoryCustomerCollaboratorRepository implements CustomerCollaboratorRepository {

    private final Map<Long, CustomerCollaborator> store = new LinkedHashMap<>();
    private long nextId = 1;

    public void add(CustomerCollaborator collaborator) {
        store.put(nextId++, collaborator);
    }

    @Override
    public Optional<CustomerCollaborator> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<CustomerCollaborator> findByUserEmail(Email email) {
        return store.values().stream()
                .filter(c -> c.userEmail().equals(email))
                .findFirst();
    }

    @Override
    public Iterable<CustomerCollaborator> findByCustomer(CustomerType customerType, String customerCode) {
        return store.values().stream()
                .filter(c -> c.customerType() == customerType && c.customerCode().equals(customerCode))
                .toList();
    }

    @Override
    public Iterable<CustomerCollaborator> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public CustomerCollaborator save(CustomerCollaborator entity) {
        store.put(nextId++, entity);
        return entity;
    }

    @Override
    public void delete(CustomerCollaborator entity) {
        store.values().removeIf(c -> c.hashCode() == entity.hashCode());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public long count() {
        return store.size();
    }
}
