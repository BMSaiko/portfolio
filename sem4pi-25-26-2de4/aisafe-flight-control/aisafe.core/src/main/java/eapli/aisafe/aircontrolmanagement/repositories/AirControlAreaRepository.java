package eapli.aisafe.aircontrolmanagement.repositories;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.generic.repositories.Repository;

import java.util.Optional;

/**
 * Repository contract for accessing and persisting {@link AirControlArea}
 * aggregate roots.
 *
 * <p>
 * This repository provides the generic persistence operations inherited from
 * {@link Repository} and adds a domain-specific lookup by
 * {@link AirControlAreaCode}.
 * </p>
 *
 * <p>
 * Air control areas are used by several functional areas of the system,
 * including airport registration, weather data registration/import,
 * weather consultation and flight-related operations.
 * </p>
 */
public interface AirControlAreaRepository extends Repository<AirControlArea, AirControlAreaCode> {

    /**
     * Finds an air control area by its business code.
     *
     * @param code air control area code to search for
     * @return an optional containing the matching air control area if found, or
     * empty otherwise
     */
    Optional<AirControlArea> findByCode(AirControlAreaCode code);
}