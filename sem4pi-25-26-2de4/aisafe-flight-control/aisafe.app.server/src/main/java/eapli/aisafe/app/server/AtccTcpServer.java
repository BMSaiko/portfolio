package eapli.aisafe.app.server;

import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.bootstrap.EngineModelBootstrap;
import eapli.aisafe.bootstrap.MasterUsersBootstrapper;
import eapli.aisafe.bootstrap.AirControlAreaBootstrap;
import eapli.aisafe.fleetmanagement.application.AddAircraftController;
import eapli.aisafe.fleetmanagement.application.DecommissionAircraftController;
import eapli.aisafe.fleetmanagement.application.ListFleetController;
import eapli.aisafe.pilotmanagement.application.AddPilotController;
import eapli.aisafe.pilotmanagement.application.ListPilotsController;
import eapli.aisafe.pilotmanagement.application.RemovePilotController;
import eapli.aisafe.flightplanmanagement.application.ValidateFlightPlanController;  // US085
import eapli.aisafe.persistence.impl.jpa.JpaUserRepository;
import eapli.aisafe.persistence.impl.jpa.JpaAirTransportCompanyRepository;
import eapli.aisafe.persistence.impl.jpa.JpaCustomerCollaboratorRepository;
import eapli.aisafe.persistence.impl.jpa.JpaAirControlAreaRepository;
import eapli.aisafe.persistence.impl.jpa.JpaAirportRepository;
import eapli.aisafe.persistence.impl.jpa.JpaAircraftRepository;
import eapli.aisafe.persistence.impl.jpa.JpaAircraftModelRepository;
import eapli.aisafe.persistence.impl.jpa.JpaFlightRouteRepository;
import eapli.aisafe.persistence.impl.jpa.JpaFlightPlanRepository;
import eapli.aisafe.persistence.impl.jpa.JpaPilotRepository;
import eapli.aisafe.remotelogging.application.RemoteLoggingService;
import eapli.aisafe.routemanagement.application.CreateFlightRouteController;
import eapli.aisafe.routemanagement.application.DeactivateFlightRouteController;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.bootstrap.EngineModelBootstrap;
import eapli.aisafe.persistence.impl.jpa.JpaEngineModelRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCP server for the Air Transport Company Collaborator application.
 *
 * <p>
 * This server listens for company client connections and delegates each
 * connection to an {@link AtccConnectionHandler}. It exposes company-side
 * operations such as fleet management, flight route creation and flight route
 * deactivation.
 * </p>
 *
 * <p>
 * The server acts as a composition root for the TCP application: it creates JPA
 * repositories, application controllers, the authentication service and the
 * remote logging service required by each connection handler.
 * </p>
 */
public class AtccTcpServer {

    /**
     * Logger used for server lifecycle and connection messages.
     */
    private static final Logger LOGGER = Logger.getLogger(AtccTcpServer.class.getName());

    /**
     * Default TCP port used by the ATCC server.
     */
    public static final int DEFAULT_PORT = 2002;

    /**
     * Default remote logging host.
     *
     * <p>
     * The value can be overridden by the {@code logging.host} system property or
     * by the {@code AISAFE_LOGGING_HOST} environment variable.
     * </p>
     */
    private static final String DEFAULT_LOGGING_HOST =
            System.getProperty("logging.host",
                    System.getenv().getOrDefault("AISAFE_LOGGING_HOST", "127.0.0.1"));

    /**
     * Default UDP port used to send remote logging events.
     *
     * <p>
     * The value can be overridden by the {@code logging.port} system property or
     * by the {@code AISAFE_LOGGING_PORT} environment variable.
     * </p>
     */
    private static final int DEFAULT_LOGGING_UDP_PORT =
            Integer.parseInt(System.getProperty("logging.port",
                    System.getenv().getOrDefault("AISAFE_LOGGING_PORT", "9999")));

    /**
     * TCP port on which the server listens.
     */
    private final int port;

    /**
     * Thread pool used to process client connections concurrently.
     */
    private final ExecutorService threadPool;

    /**
     * Flag that controls the server accept loop.
     */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Service used to send remote access log events.
     */
    private final RemoteLoggingService remoteLoggingService;

    /**
     * Authentication service shared by connection handlers.
     */
    private final AuthService authService;

    /**
     * Controller used to add aircraft to the company fleet.
     */
    private final AddAircraftController addAircraftController;

    /**
     * Controller used to decommission aircraft.
     */
    private final DecommissionAircraftController decommissionAircraftController;

    /**
     * Controller used to list the company fleet.
     */
    private final ListFleetController listFleetController;

    /**
     * Controller used to create flight routes.
     */
    private final CreateFlightRouteController createFlightRouteController;

    /**
     * Controller used to deactivate flight routes.
     */
    private final DeactivateFlightRouteController deactivateFlightRouteController;

    /**
     * Controller used to validate flight plans (US085).
     */
    private final ValidateFlightPlanController validateFlightPlanController;  // US085

    private final AddPilotController addPilotController;
    private final ListPilotsController listPilotsController;
    private final RemovePilotController removePilotController;
    private final AircraftModelRepository aircraftModelRepo;

    /**
     * Server socket used to accept incoming TCP connections.
     */
    private ServerSocket serverSocket;

    /**
     * Creates a server using the default TCP port.
     */
    public AtccTcpServer() {
        this(DEFAULT_PORT);
    }

    /**
     * Creates a server using the given TCP port.
     *
     * @param port TCP port to listen on
     */
    public AtccTcpServer(final int port) {
        this.port = port;
        this.threadPool = Executors.newCachedThreadPool();
        this.remoteLoggingService = new RemoteLoggingService(DEFAULT_LOGGING_HOST, DEFAULT_LOGGING_UDP_PORT);

        String pu = "aisafePU";

        JpaUserRepository userRepo = new JpaUserRepository();
        JpaAircraftRepository aircraftRepo = new JpaAircraftRepository(pu);
        JpaAircraftModelRepository aircraftModelRepo = new JpaAircraftModelRepository(pu);
        JpaEngineModelRepository engineRepo = new JpaEngineModelRepository(pu);
        JpaAirTransportCompanyRepository companyRepo = new JpaAirTransportCompanyRepository(pu);
        JpaCustomerCollaboratorRepository collaboratorRepo = new JpaCustomerCollaboratorRepository(pu);
        JpaAirControlAreaRepository areaRepo = new JpaAirControlAreaRepository(pu);
        JpaAirportRepository airportRepo = new JpaAirportRepository(pu);
        JpaFlightRouteRepository flightRouteRepo = new JpaFlightRouteRepository(pu);
        JpaFlightPlanRepository flightPlanRepo = new JpaFlightPlanRepository(pu);
        JpaPilotRepository pilotRepo = new JpaPilotRepository(pu);

        bootstrapReferenceData(userRepo, companyRepo, collaboratorRepo, areaRepo, airportRepo, engineRepo, aircraftModelRepo);

        this.authService = new AuthService(userRepo);
        this.listFleetController = new ListFleetController(aircraftRepo, collaboratorRepo, authService);
        this.addAircraftController = new AddAircraftController(
                aircraftRepo,
                aircraftModelRepo,
                collaboratorRepo,
                companyRepo,
                authService);
        this.decommissionAircraftController = new DecommissionAircraftController(aircraftRepo, authService);
        this.createFlightRouteController = new CreateFlightRouteController(
                flightRouteRepo,
                airportRepo,
                collaboratorRepo,
                companyRepo,
                authService);
        this.deactivateFlightRouteController = new DeactivateFlightRouteController(
                flightRouteRepo,
                flightPlanRepo,
                collaboratorRepo,
                companyRepo,
                authService);

        // US085 — ValidateFlightPlanController needs only the FlightPlan repository
        this.validateFlightPlanController =
                new ValidateFlightPlanController(flightPlanRepo);              // US085
        this.addPilotController = new AddPilotController(
                this.authService, pilotRepo, userRepo, aircraftModelRepo, collaboratorRepo, companyRepo
        );

        this.listPilotsController = new ListPilotsController(
                this.authService, pilotRepo, collaboratorRepo, companyRepo
        );

        this.removePilotController = new RemovePilotController(
                this.authService, pilotRepo, collaboratorRepo, companyRepo, flightPlanRepo
        );

        this.aircraftModelRepo = aircraftModelRepo;
    }

    /**
     * Loads reference data required by the TCP server.
     *
     * <p>
     * The bootstrap is treated as non-fatal: if it fails, the server logs a
     * warning and continues starting. This keeps the TCP server available even
     * when some demo/reference data already exists or cannot be created.
     * </p>
     *
     * @param userRepo user repository
     * @param companyRepo air transport company repository
     * @param collaboratorRepo customer collaborator repository
     * @param areaRepo air control area repository
     * @param airportRepo airport repository
     */
    private void bootstrapReferenceData(JpaUserRepository userRepo,
                                        JpaAirTransportCompanyRepository companyRepo,
                                        JpaCustomerCollaboratorRepository collaboratorRepo,
                                        JpaAirControlAreaRepository areaRepo,
                                        JpaAirportRepository airportRepo,
                                        JpaEngineModelRepository engineRepo,
                                        JpaAircraftModelRepository aircraftModelRepo) {
        try {
            LOGGER.info("[ATCC Server] Bootstrapping reference data...");
            new MasterUsersBootstrapper(userRepo, companyRepo, collaboratorRepo, areaRepo).bootstrap();
            new AirControlAreaBootstrap(areaRepo, airportRepo).bootstrap();
            new EngineModelBootstrap(engineRepo, aircraftModelRepo).load();
            ensureAtccCollaborator(collaboratorRepo);
            LOGGER.info("[ATCC Server] Bootstrap complete.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[ATCC Server] Bootstrap error (non-fatal)", e);
        }
    }

    /**
     * Ensures that the default ATCC user has a customer collaborator record.
     *
     * <p>
     * This is needed so server-side controllers can resolve the authenticated
     * user to the corresponding air transport company.
     * </p>
     *
     * @param collaboratorRepo repository used to check and create the
     *                         collaborator record
     */
    private void ensureAtccCollaborator(JpaCustomerCollaboratorRepository collaboratorRepo) {
        try {
            Email atccEmail = new Email("atcc@airline.com");
            if (collaboratorRepo.findByUserEmail(atccEmail).isEmpty()) {
                CustomerCollaborator atcc = new CustomerCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "TAP",
                        atccEmail,
                        "Operations Collaborator");
                collaboratorRepo.save(atcc);
                LOGGER.info("[ATCC Server] Created CustomerCollaborator for atcc@airline.com");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[ATCC Server] Error ensuring atcc collaborator", e);
        }
    }

    /**
     * Starts the TCP server and accepts client connections.
     *
     * <p>
     * Each accepted client socket is wrapped in an {@link AtccConnectionHandler}
     * and submitted to the server thread pool for concurrent processing.
     * </p>
     *
     * @throws IOException if the server socket cannot be opened
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running.set(true);
        LOGGER.info(() -> "[ATCC Server] Started on port " + port);

        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info(() -> "[ATCC Server] Client connected: " +
                        clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                AtccConnectionHandler handler = new AtccConnectionHandler(
                        clientSocket,
                        authService,
                        null,
                        addAircraftController,
                        decommissionAircraftController,
                        listFleetController,
                        createFlightRouteController,
                        deactivateFlightRouteController,
                        validateFlightPlanController,   // US085
                        addPilotController,
                        listPilotsController,
                        removePilotController,
                        aircraftModelRepo,
                        remoteLoggingService);

                threadPool.submit(handler);
            } catch (IOException e) {
                if (running.get()) {
                    LOGGER.log(Level.WARNING, "[ATCC Server] Error accepting connection", e);
                }
            }
        }
    }

    /**
     * Stops the TCP server.
     *
     * <p>
     * This method stops the accept loop, shuts down the connection thread pool
     * and closes the server socket if it is open.
     * </p>
     */
    public void stop() {
        running.set(false);
        threadPool.shutdown();

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "[ATCC Server] Error closing server socket", e);
            }
        }

        LOGGER.info("[ATCC Server] Stopped.");
    }

    /**
     * Starts the ATCC TCP server from the command line.
     *
     * <p>
     * If a port is provided as the first argument, the server attempts to use
     * it. If the argument is missing or invalid, the default port is used.
     * </p>
     *
     * @param args optional command-line arguments; first argument may be the TCP
     *             port
     * @throws IOException if the server cannot start
     */
    public static void main(final String[] args) throws IOException {
        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid port. Using default: " + DEFAULT_PORT);
            }
        }

        AtccTcpServer server = new AtccTcpServer(port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}