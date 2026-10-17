package eapli.aisafe.pilotmanagement.repositories;

import eapli.aisafe.generic.repositories.Repository;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.usermanagement.domain.Email;

import java.util.List;
import java.util.Optional;

public interface PilotRepository extends Repository<Pilot, Long> {

    Optional<Pilot> findByEmail(Email email);

    List<Pilot> findByCompany(AirTransportCompany company);

    boolean existsByEmail(Email email);
}