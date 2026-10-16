package eapli.aisafe.organization.repositories;

import eapli.aisafe.generic.repositories.Repository;
import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;

import java.util.Optional;

/**
 * Repository interface for {@link AirControlArea} entities.
 *
 * <p>
 * Following the eCafeteria pattern, this interface extends the base
 * {@link Repository} interface and the save() method returns void.
 * </p>
 */
public interface AirControlAreaRepository extends Repository<AirControlArea, Long> {

    /**
     * Finds an air control area by area code.
     *
     * @param areaCode area code
     * @return optional air control area
     */
    Optional<AirControlArea> findByAreaCode(String areaCode);
}