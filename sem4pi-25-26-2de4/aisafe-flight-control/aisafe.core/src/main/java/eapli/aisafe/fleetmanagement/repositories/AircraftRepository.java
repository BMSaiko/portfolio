package eapli.aisafe.fleetmanagement.repositories;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.framework.domain.repositories.DomainRepository;
import eapli.aisafe.fleetmanagement.domain.Aircraft;

import java.util.Optional;

public interface AircraftRepository extends DomainRepository<String, Aircraft> {
    boolean existsByRegistration(String registration);

    /**
     * Returns true if at least one Aircraft instance uses the given AircraftModel.
     * Used by US058 to prevent removing the last certified engine from a model
     * that has actual aircraft in operation.
     */
    boolean existsByAircraftModelId(AircraftModelId id);

    /**
     * Returns all aircraft belonging to a specific Air Transport Company.
     */
    Iterable<Aircraft> findByCompany(String companyIataCode);

    Optional<Aircraft> findByRegistration(String registration);
}