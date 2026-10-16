package eapli.aisafe.bootstrap;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.generic.domain.PhoneNumber;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Bootstrapper responsible for creating initial master data.
 *
 * <p>
 * This bootstrapper creates initial system users, air transport companies and
 * customer collaborators. It supports US030, US060 and US061 by ensuring that
 * the application has valid initial data when it starts.
 * </p>
 *
 * <p>
 * The bootstrap process is idempotent at a basic level: it checks whether data
 * already exists before inserting the default records.
 * </p>
 */
public class MasterUsersBootstrapper {

    private static final Logger LOGGER = Logger.getLogger(MasterUsersBootstrapper.class.getName());

    private final UserRepository userRepo;
    private final AirTransportCompanyRepository companyRepo;
    private final CustomerCollaboratorRepository collaboratorRepo;
    private final AirControlAreaRepository areaRepo;

    /**
     * Creates the master data bootstrapper.
     *
     * @param userRepo         repository used to manage users
     * @param companyRepo      repository used to manage air transport companies
     * @param collaboratorRepo repository used to manage customer collaborators
     */
    public MasterUsersBootstrapper(UserRepository userRepo,
                                   AirTransportCompanyRepository companyRepo,
                                   CustomerCollaboratorRepository collaboratorRepo,
                                   AirControlAreaRepository areaRepo) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;
        this.collaboratorRepo = collaboratorRepo;
        this.areaRepo = areaRepo;
    }

    /**
     * Executes all bootstrap steps.
     */
    public void bootstrap() {
        bootstrapUsers();
        bootstrapCompanies();
        bootstrapAirControlAreas();
        bootstrapCustomerCollaborators();
    }

    /**
     * Creates the initial system users if no users exist yet.
     */
    private void bootstrapUsers() {
        if (userRepo.findAll().iterator().hasNext()) {
            return;
        }

        LOGGER.info("Bootstrapping master users...");

        createUser("admin@aisafe.com", "Admin123", "System Admin", "+35191111111",
                Role.ADMIN, Role.BACKOFFICE_OPERATOR);

        createUser("backoffice@aisafe.com", "Back1234", "Backoffice Operator", "+35192222222",
                Role.BACKOFFICE_OPERATOR);

        createUser("weather@aisafe.com", "Weather123", "Weather Person", "+35193333333",
                Role.WEATHER_PERSON);

        createUser("atcc@airline.com", "Atcc1234", "Airline Collaborator", "+35194444444",
                Role.AIR_TRANSPORT_COMPANY_COLLABORATOR);

        createUser("pilot@aisafe.com", "Pilot123", "Pilot", "+35195555555",
                Role.PILOT);

        createUser("fco@control.com", "Fco12345", "Flight Control Operator", "+35196666666",
                Role.FLIGHT_CONTROL_OPERATOR);
    }

    /**
     * Creates initial air transport companies if no companies exist yet.
     *
     * <p>
     * This supports US060, which requires air transport company registration to
     * also be achievable by bootstrap.
     * </p>
     */
    private void bootstrapCompanies() {
        if (companyRepo.findAll().iterator().hasNext()) {
            return;
        }

        LOGGER.info("Bootstrapping air transport companies...");

        companyRepo.save(new AirTransportCompany("TAP Air Portugal", "TP", "TAP"));
        companyRepo.save(new AirTransportCompany("Ryanair", "FR", "RYR"));
        companyRepo.save(new AirTransportCompany("EasyJet Europe", "EC", "EJU"));
    }

    /**
     * Creates initial customer collaborators if no collaborators exist yet.
     *
     * <p>
     * This supports US061, which requires customer collaborator registration to
     * also be achievable by bootstrap.
     * </p>
     */
    private void bootstrapCustomerCollaborators() {
        if (collaboratorRepo.findAll().iterator().hasNext()) {
            return;
        }

        LOGGER.info("Bootstrapping customer collaborators...");

        createCollaborator(new CollaboratorData(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "tap.collab@aisafe.com",
                "Tap12345",
                "TAP Collaborator",
                "+351977777777",
                "Operations Collaborator",
                Role.AIR_TRANSPORT_COMPANY_COLLABORATOR
        ));

        createCollaborator(new CollaboratorData(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "RYR",
                "ryanair.collab@aisafe.com",
                "Ryan1234",
                "Ryanair Collaborator",
                "+351977777778",
                "Fleet Collaborator",
                Role.AIR_TRANSPORT_COMPANY_COLLABORATOR
        ));

        createCollaborator(new CollaboratorData(
                CustomerType.AIR_CONTROL_AREA,
                "LIS_AREA",
                "lis.control@aisafe.com",
                "Lis12345",
                "Lisbon Control Collaborator",
                "+351988888888",
                "Flight Control Operator",
                Role.FLIGHT_CONTROL_OPERATOR
        ));

        createCollaborator(new CollaboratorData(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "atcc@airline.com",
                "Atcc1234",
                "Airline Collaborator",
                "+35194444444",
                "Operations Collaborator",
                Role.AIR_TRANSPORT_COMPANY_COLLABORATOR
        ));
    }

    private void bootstrapAirControlAreas() {
        if (areaRepo.findAll().iterator().hasNext()) return;
        LOGGER.info("Bootstrapping air control areas...");
        areaRepo.save(new AirControlArea(AirControlAreaCode.of("LIS_AREA"), "Lisbon Area", 500.0, new GeoBoundary(38.0, -9.0, 39.0, -8.0)));
        areaRepo.save(new AirControlArea(AirControlAreaCode.of("OPO_AREA"), "Porto Area", 450.0, new GeoBoundary(41.0, -8.7, 41.5, -8.3)));
    }

    /**
     * Creates a system user if the email is not already registered.
     *
     * @param email user's email
     * @param pwd   user's plain password, which will be hashed by the domain
     * @param name  user's name
     * @param phone user's phone number
     * @param roles roles assigned to the user
     */
    private void createUser(String email, String pwd, String name, String phone, Role... roles) {
        Email userEmail = new Email(email);

        if (userRepo.existsByEmail(userEmail)) {
            return;
        }

        User user = new User(userEmail, pwd, UserName.of(name), PhoneNumber.of(phone), Set.of(roles));
        userRepo.save(user);
    }

    /**
     * Creates a customer collaborator and its system user.
     *
     * <p>
     * If the user already exists, it is reused. If the collaborator association
     * already exists, it is not duplicated.
     * </p>
     *
     * @param data data required to create the collaborator and its user
     */
    private void createCollaborator(CollaboratorData data) {
        Email userEmail = new Email(data.email);

        if (!userRepo.existsByEmail(userEmail)) {
            User user = new User(userEmail, data.password, UserName.of(data.name), PhoneNumber.of(data.phone), Set.of(data.role));
            userRepo.save(user);
        }

        if (collaboratorRepo.findByUserEmail(userEmail).isPresent()) {
            return;
        }

        CustomerCollaborator collaborator = new CustomerCollaborator(
                data.customerType,
                data.customerCode,
                userEmail,
                data.position
        );

        collaboratorRepo.save(collaborator);
    }

    /**
     * Immutable data holder for collaborator creation.
     * Reduces the number of parameters in {@link #createCollaborator(CollaboratorData)}.
     */
    private record CollaboratorData(
            CustomerType customerType,
            String customerCode,
            String email,
            String password,
            String name,
            String phone,
            String position,
            Role role
    ) {}
}
