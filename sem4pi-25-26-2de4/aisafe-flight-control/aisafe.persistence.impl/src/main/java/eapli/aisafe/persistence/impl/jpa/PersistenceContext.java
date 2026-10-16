package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.weather.repositories.WeatherDataRepository;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;

/**
 * Central access point for persistence repositories.
 *
 * <p>
 * This class provides a simple repository factory backed by JPA repositories.
 * It returns interfaces instead of concrete classes, following dependency
 * inversion.
 * </p>
 */
public final class PersistenceContext {
    private static RepositoryFactory factory;

    private PersistenceContext() {}

    public interface RepositoryFactory {
        UserRepository users();
        AirTransportCompanyRepository airTransportCompanies();
        CustomerCollaboratorRepository customerCollaborators();
        AirControlAreaRepository airControlAreas();
        WeatherDataRepository weatherData();
        AircraftRepository aircrafts();
        AircraftModelRepository aircraftModels();
    }

    public static RepositoryFactory repositories() {
        if (factory == null) {
            factory = new JpaRepositoryFactory();
        }
        return factory;
    }

    static class JpaRepositoryFactory implements RepositoryFactory {
        private static final String PERSISTENCE_UNIT = "aisafePU";

        @Override
        public UserRepository users() {
            return new JpaUserRepository();
        }

        @Override
        public AirTransportCompanyRepository airTransportCompanies() {
            return new JpaAirTransportCompanyRepository(PERSISTENCE_UNIT);
        }

        @Override
        public CustomerCollaboratorRepository customerCollaborators() {
            return new JpaCustomerCollaboratorRepository(PERSISTENCE_UNIT);
        }

        @Override
        public AirControlAreaRepository airControlAreas() {
            return new JpaAirControlAreaRepository(PERSISTENCE_UNIT);
        }

        @Override
        public WeatherDataRepository weatherData() {
            return new JpaWeatherDataRepository(PERSISTENCE_UNIT);
        }

        @Override
        public AircraftRepository aircrafts() { return new JpaAircraftRepository(PERSISTENCE_UNIT); }

        @Override
        public AircraftModelRepository aircraftModels() { return new JpaAircraftModelRepository(PERSISTENCE_UNIT); }
    }
}