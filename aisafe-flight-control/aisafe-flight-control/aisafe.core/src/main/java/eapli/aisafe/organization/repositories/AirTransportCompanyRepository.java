package eapli.aisafe.organization.repositories;

import eapli.aisafe.generic.repositories.Repository;
import eapli.aisafe.organization.domain.AirTransportCompany;

import java.util.Optional;

/**
 * Repository interface for {@link AirTransportCompany} entities.
 *
 * <p>
 * This repository supports US060 - Register an air transport company, providing
 * persistence and lookup operations required to enforce IATA and ICAO
 * uniqueness.
 * </p>
 *
 * <p>
 * Following the eCafeteria pattern, this interface extends the base
 * {@link Repository} interface and the save() method returns void.
 * </p>
 */
public interface AirTransportCompanyRepository extends Repository<AirTransportCompany, Long> {

    /**
     * Finds an air transport company by IATA code.
     *
     * @param iataCode IATA code
     * @return optional company
     */
    Optional<AirTransportCompany> findByIataCode(String iataCode);

    /**
     * Finds an air transport company by ICAO code.
     *
     * @param icaoCode ICAO code
     * @return optional company
     */
    Optional<AirTransportCompany> findByIcaoCode(String icaoCode);

    /**
     * Checks whether a company with the given IATA code exists.
     *
     * @param iataCode IATA code
     * @return {@code true} if the IATA code is already registered
     */
    boolean existsByIataCode(String iataCode);

    /**
     * Checks whether a company with the given ICAO code exists.
     *
     * @param icaoCode ICAO code
     * @return {@code true} if the ICAO code is already registered
     */
    boolean existsByIcaoCode(String icaoCode);
}