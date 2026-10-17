package eapli.aisafe.organization.application;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

/**
 * Application controller responsible for registering air transport companies.
 *
 * <p>
 * This controller supports US060 - Register an air transport company.
 * It enforces authentication, authorization, IATA uniqueness and ICAO
 * uniqueness before creating and saving the company.
 * </p>
 *
 * <p>
 * Only users with role {@link Role#BACKOFFICE_OPERATOR} or {@link Role#ADMIN}
 * are allowed to register air transport companies.
 * </p>
 */
public class RegisterAirTransportCompanyController {

    private final AuthService auth;
    private final AirTransportCompanyRepository companyRepo;

    /**
     * Creates the register air transport company controller.
     *
     * @param auth authentication and authorization service
     * @param companyRepo repository used to access air transport companies
     */
    public RegisterAirTransportCompanyController(AuthService auth, AirTransportCompanyRepository companyRepo) {
        this.auth = auth;
        this.companyRepo = companyRepo;
    }

    /**
     * Registers a new air transport company.
     *
     * <p>
     * The IATA and ICAO codes are normalized to uppercase before checking
     * uniqueness and creating the domain entity.
     * </p>
     *
     * @param name company name
     * @param iataCode IATA code with exactly two letters
     * @param icaoCode ICAO code with two or three letters
     * @return saved air transport company
     * @throws SecurityException if the current user is not authenticated or does
     *                          not have the required role
     * @throws IllegalArgumentException if IATA or ICAO already exists, or if the
     *                                  domain entity validation fails
     */
    public void registerCompany(String name, String iataCode, String icaoCode) {
        auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

        String iata = iataCode.trim().toUpperCase();
        String icao = icaoCode.trim().toUpperCase();

        if (companyRepo.existsByIataCode(iata)) {
            throw new IllegalArgumentException("IATA code already exists");
        }

        if (companyRepo.existsByIcaoCode(icao)) {
            throw new IllegalArgumentException("ICAO code already exists");
        }

        AirTransportCompany company = new AirTransportCompany(name, iata, icao);
        companyRepo.save(company);
    }
}