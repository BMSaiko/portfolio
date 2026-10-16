package eapli.aisafe.app.server;

import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.fleetmanagement.application.AddAircraftController;
import eapli.aisafe.fleetmanagement.application.DecommissionAircraftController;
import eapli.aisafe.fleetmanagement.application.ListFleetController;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.dto.AircraftRegistrationDTO;
import eapli.aisafe.flightplanmanagement.application.ValidateFlightPlanController;  // US085
import eapli.aisafe.pilotmanagement.application.AddPilotController;
import eapli.aisafe.pilotmanagement.application.ListPilotsController;
import eapli.aisafe.pilotmanagement.application.RemovePilotController;
import eapli.aisafe.remotelogging.application.RemoteLoggingService;
import eapli.aisafe.remotelogging.domain.EventType;
import eapli.aisafe.remotelogging.domain.ServiceType;
import eapli.aisafe.routemanagement.application.CreateFlightRouteController;
import eapli.aisafe.routemanagement.application.DeactivateFlightRouteController;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.dto.FlightRouteRegistrationDTO;
import eapli.aisafe.tcpprotocol.AtccProtocol;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import pt.isep.sem4pi.lprog.application.US085Result;  // US085

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connection handler for one Air Transport Company Collaborator TCP client.
 *
 * <p>
 * Each instance handles a single client socket. It reads line-based ATCC
 * protocol commands, parses them through {@link AtccProtocol}, executes the
 * corresponding application controller and writes a single-line response back to
 * the client.
 * </p>
 *
 * <p>
 * The handler also manages authentication state for the connected client. All
 * business commands require a successful authentication command first.
 * </p>
 */
public class AtccConnectionHandler implements Runnable {

    /**
     * Logger used for connection, authentication and processing messages.
     */
    private static final Logger LOGGER = Logger.getLogger(AtccConnectionHandler.class.getName());

    /**
     * Client socket handled by this connection handler.
     */
    private final Socket clientSocket;

    /**
     * Authentication service used to authenticate and authorize the client.
     */
    private final AuthService authService;

    /**
     * Optional user repository dependency kept for compatibility with older
     * construction paths.
     */
    private final UserRepository userRepository;

    /**
     * Controller used to add aircraft to the authenticated collaborator's
     * company fleet.
     */
    private final AddAircraftController addAircraftController;

    /**
     * Controller used to decommission aircraft.
     */
    private final DecommissionAircraftController decommissionAircraftController;

    /**
     * Controller used to list the authenticated collaborator's company fleet.
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
     * Service used to log remote access events.
     */
    private final RemoteLoggingService remoteLoggingService;

    /**
     * Reader used to receive commands from the client.
     */
    private BufferedReader reader;

    /**
     * Writer used to send responses to the client.
     */
    private BufferedWriter writer;

    /**
     * Email address of the authenticated user for this connection.
     *
     * <p>
     * This value remains {@code null} until authentication succeeds.
     * </p>
     */
    private String authenticatedUser;

    /**
     * Creates a connection handler with only a client socket.
     *
     * <p>
     * This constructor is mainly useful for tests or legacy paths. Without the
     * required controllers and authentication service, business commands will be
     * rejected.
     * </p>
     *
     * @param clientSocket client socket to handle
     */
    public AtccConnectionHandler(final Socket clientSocket) {
        this(clientSocket, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    /**
     * Creates a connection handler with remote logging support only.
     *
     * @param clientSocket client socket to handle
     * @param remoteLoggingService remote logging service
     */
    public AtccConnectionHandler(final Socket clientSocket,
                                 final RemoteLoggingService remoteLoggingService) {
        this(clientSocket, null, null, null, null, null, null, null, null, null, null, null, null, remoteLoggingService);
    }

    /**
     * Creates a connection handler with authentication and remote logging
     * support.
     *
     * @param clientSocket client socket to handle
     * @param authService authentication service
     * @param remoteLoggingService remote logging service
     */
    public AtccConnectionHandler(final Socket clientSocket,
                                 final AuthService authService,
                                 final RemoteLoggingService remoteLoggingService) {
        this(clientSocket, authService, null, null, null, null, null, null, null, null, null, null, null, remoteLoggingService);
    }

    /**
     * Creates a fully configured connection handler.
     *
     * @param clientSocket client socket to handle
     * @param authService authentication service
     * @param userRepository optional user repository dependency
     * @param addAircraftController controller for adding aircraft
     * @param decommissionAircraftController controller for decommissioning aircraft
     * @param listFleetController controller for fleet listing
     * @param createFlightRouteController controller for route creation
     * @param deactivateFlightRouteController controller for route deactivation
     * @param validateFlightPlanController controller for flight plan validation (US085)
     * @param remoteLoggingService remote logging service
     */
    public AtccConnectionHandler(final Socket clientSocket,
                                 final AuthService authService,
                                 final UserRepository userRepository,
                                 final AddAircraftController addAircraftController,
                                 final DecommissionAircraftController decommissionAircraftController,
                                 final ListFleetController listFleetController,
                                 final CreateFlightRouteController createFlightRouteController,
                                 final DeactivateFlightRouteController deactivateFlightRouteController,
                                 final ValidateFlightPlanController validateFlightPlanController,
                                 final AddPilotController addPilotController,
                                 final ListPilotsController listPilotsController,
                                 final RemovePilotController removePilotController,
                                 final AircraftModelRepository aircraftModelRepository,
                                 final RemoteLoggingService remoteLoggingService) {
        this.clientSocket = clientSocket;
        this.authService = authService;
        this.userRepository = userRepository;
        this.addAircraftController = addAircraftController;
        this.decommissionAircraftController = decommissionAircraftController;
        this.listFleetController = listFleetController;
        this.createFlightRouteController = createFlightRouteController;
        this.deactivateFlightRouteController = deactivateFlightRouteController;
        this.validateFlightPlanController = validateFlightPlanController;  // US085
        this.addPilotController = addPilotController;
        this.listPilotsController = listPilotsController;
        this.removePilotController = removePilotController;
        this.aircraftModelRepo = aircraftModelRepository;
        this.remoteLoggingService = remoteLoggingService;
    }

    /**
     * Handles the client connection.
     *
     * <p>
     * The method initializes input/output streams, reads protocol commands until
     * the client disconnects or sends logout, writes responses, logs remote
     * access events and closes resources at the end.
     * </p>
     */
    @Override
    public void run() {
        String clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();

        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));

            LOGGER.info(() -> "[ATCC Handler " + clientInfo + "] Connection established.");

            String line;
            while ((line = reader.readLine()) != null) {
                AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(line);
                String response = processMessage(msg);

                writer.write(response);
                writer.newLine();
                writer.flush();

                if (msg.isLogout()) {
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "[ATCC Handler " + clientInfo + "] Connection error", e);
        } finally {
            if (authenticatedUser != null) {
                logRemoteAccess(authenticatedUser, EventType.DISCONNECT);
            }

            closeQuietly();
            LOGGER.info(() -> "[ATCC Handler " + clientInfo + "] Disconnected.");
        }
    }

    /**
     * Processes one parsed ATCC protocol message.
     *
     * <p>
     * Authentication messages are handled first. All other commands require a
     * previously authenticated user. Unknown commands and validation errors are
     * converted into protocol error responses.
     * </p>
     *
     * @param msg parsed protocol message
     * @return protocol response line
     */
    private String processMessage(final AtccProtocol.ProtocolMessage msg) {
        try {
            if (msg.isAuth()) return handleAuth(msg);

            if (authenticatedUser == null) {
                return AtccProtocol.buildErrorResponse("Not authenticated. Send AUTH first.");
            }

            if (msg.isAddAircraft()) return handleAddAircraft(msg);
            if (msg.isDecommissionAircraft()) return handleDecommissionAircraft(msg);
            if (msg.isListFleet()) return handleListFleet(msg);
            if (msg.isCreateRoute()) return handleCreateRoute(msg);
            if (msg.isDeactivateRoute()) return handleDeactivateRoute(msg);
            if (msg.isValidateFlightPlan()) return handleValidateFlightPlan(msg);  // US085
            if (msg.isAddPilot()) return handleAddPilot(msg);
            if (msg.isListPilots()) return handleListPilots(msg);
            if (msg.isRemovePilot()) return handleRemovePilot(msg);
            if (msg.isListModels()) return handleListModels(msg);

            if (msg.isLogout()) {
                if (authenticatedUser != null) {
                    logRemoteAccess(authenticatedUser, EventType.LOGOUT);
                    authenticatedUser = null;
                }

                return AtccProtocol.BYE;
            }

            return AtccProtocol.buildErrorResponse("Unknown command: " + msg.command());
        } catch (IllegalArgumentException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing: " + msg.command(), e);
            return AtccProtocol.buildErrorResponse("Internal error: " + e.getMessage());
        }
    }

    /**
     * Handles an authentication command.
     *
     * <p>
     * The command must contain email and password parameters. Successful
     * authentication is accepted only if the user has the
     * {@link Role#AIR_TRANSPORT_COMPANY_COLLABORATOR} role.
     * </p>
     *
     * @param msg authentication protocol message
     * @return authentication success or failure response
     */
    private String handleAuth(final AtccProtocol.ProtocolMessage msg) {
        LOGGER.info("[AUTH] command=" + msg.command()
                + " params=" + msg.params()
                + " size=" + msg.params().size());

        if (msg.params().size() < 2) {
            LOGGER.info("[AUTH] FAIL - not enough params");
            return AtccProtocol.AUTH_FAIL;
        }

        String email = msg.params().get(0);
        String password = msg.params().get(1);

        LOGGER.info("[AUTH] email=" + email
                + " password=" + password
                + " authService=" + (authService != null));

        if (authService == null) {
            LOGGER.warning("[AUTH] No AuthService configured - rejecting login for " + email);
            logRemoteAccess(email, EventType.LOGIN_FAILURE);
            return AtccProtocol.AUTH_FAIL;
        }

        if (authService.login(email, password).isPresent()) {
            try {
                authService.ensureHasAnyRole(
                        Role.AIR_TRANSPORT_COMPANY_COLLABORATOR, Role.PILOT);  // US085 adds PILOT
                authenticatedUser = email;
                logRemoteAccess(email, EventType.LOGIN_SUCCESS);
                return AtccProtocol.AUTH_OK + " " + email;
            } catch (SecurityException se) {
                logRemoteAccess(email, EventType.LOGIN_FAILURE);
                return AtccProtocol.AUTH_FAIL;
            }
        }

        logRemoteAccess(email, EventType.LOGIN_FAILURE);
        return AtccProtocol.AUTH_FAIL;
    }

    // ── US085 — Validate Flight Plan ──────────────────────────────────────────

    /**
     * Handles a validate-flight-plan command (US085).
     *
     * <p>
     * Only users with the {@link Role#PILOT} role may invoke this command.
     * The payload must be {@code <flightPlanId>|<dslContent>}.
     * </p>
     *
     * @param msg protocol message containing the flight plan validation payload
     * @return protocol response with validation result or error information
     */
    private String handleValidateFlightPlan(final AtccProtocol.ProtocolMessage msg) {
        // Role check — only PILOTs may validate flight plans
        try {
            authService.ensureHasAnyRole(Role.PILOT);
        } catch (SecurityException se) {
            return AtccProtocol.buildErrorResponse(
                    "Access denied. PILOT role required for VALIDATE_FLIGHT_PLAN.");
        }

        if (validateFlightPlanController == null) {
            return AtccProtocol.buildErrorResponse(
                    "ValidateFlightPlanController not available.");
        }

        if (msg.params().isEmpty()) {
            return AtccProtocol.buildErrorResponse(
                    "VALIDATE_FLIGHT_PLAN requires payload: <flightPlanId>|<dslContent>");
        }

        try {
            // Single param contains: "<flightPlanId>|<dslContent>"
            String[] parts      = AtccProtocol.parseValidateFlightPlanPayload(msg.params().get(0));
            String flightPlanId = parts[0]; // may be blank → validate without persisting
            String dslContent   = parts[1];

            US085Result result = validateFlightPlanController.validate(dslContent, flightPlanId);

            if (result.isSuccess()) {
                String suffix = flightPlanId.isBlank()
                        ? ""
                        : " Plan " + flightPlanId + " marked as VALIDATED.";
                return AtccProtocol.buildOkResponse(
                        "Flight plan passed all validation checks." + suffix);
            }

            // Build a concise error summary for the TCP response
            StringBuilder sb = new StringBuilder();
            switch (result.getState()) {
                case DSL_FAILURE -> {
                    sb.append("DSL validation failed (")
                            .append(result.getDslErrors().size()).append(" error(s)): ");
                    result.getDslErrors().forEach(e ->
                            sb.append("[")
                                    .append(e.getType())
                                    .append(" L").append(e.getLine())
                                    .append("] ").append(e.getMessage())
                                    .append(" | "));
                }
                case PHYSICAL_FAILURE -> {
                    sb.append("Physical test failed (")
                            .append(result.getPhysicalIssues().size()).append(" issue(s)): ");
                    result.getPhysicalIssues().forEach(i ->
                            sb.append("[").append(i.getField())
                                    .append("] ").append(i.getMessage())
                                    .append(" | "));
                }
                case EXECUTION_ERROR ->
                        sb.append("Execution error: ").append(result.getErrorMessage());
                case IO_ERROR ->
                        sb.append("IO error: ").append(result.getErrorMessage());
                default ->
                        sb.append("Validation failed.");
            }

            return AtccProtocol.buildErrorResponse(sb.toString().trim());

        } catch (IllegalArgumentException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing VALIDATE_FLIGHT_PLAN", e);
            return AtccProtocol.buildErrorResponse("Internal error: " + e.getMessage());
        }
    }

    // ── Fleet handlers ────────────────────────────────────────────────────────

    /**
     * Handles an add-aircraft command.
     *
     * @param msg protocol message containing the aircraft registration payload
     * @return protocol response with success or error information
     */
    private String handleAddAircraft(final AtccProtocol.ProtocolMessage msg) {
        if (addAircraftController == null) {
            return AtccProtocol.buildErrorResponse("AddAircraftController not available");
        }

        try {
            String payload = String.join(",", msg.params());
            AircraftRegistrationDTO dto = AtccProtocol.parseAddAircraftPayload(payload);
            Aircraft aircraft = addAircraftController.addAircraft(dto);

            return AtccProtocol.buildOkResponse("Aircraft " + aircraft.identity() + " added.");
        } catch (IllegalArgumentException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        }
    }

    /**
     * Handles a decommission-aircraft command.
     *
     * @param msg protocol message containing the aircraft registration
     * @return protocol response with success or error information
     */
    private String handleDecommissionAircraft(final AtccProtocol.ProtocolMessage msg) {
        if (decommissionAircraftController == null) {
            return AtccProtocol.buildErrorResponse("DecommissionAircraftController not available");
        }

        try {
            Aircraft aircraft = decommissionAircraftController.decommissionAircraft(msg.params().get(0));
            return AtccProtocol.buildOkResponse("Aircraft " + aircraft.identity() + " decommissioned.");
        } catch (IllegalArgumentException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        }
    }

    /**
     * Handles a list-fleet command.
     *
     * <p>
     * Supported filters are model, maker, capacity, age and all. The current
     * response reports the number of matching aircraft.
     * </p>
     *
     * @param msg protocol message containing optional fleet filter parameters
     * @return protocol response with the fleet count or an error
     */
    private String handleListFleet(final AtccProtocol.ProtocolMessage msg) {
        if (listFleetController == null) {
            return AtccProtocol.buildErrorResponse("ListFleetController not available");
        }

        try {
            String filterType = msg.params().isEmpty() ? "ALL" : msg.params().get(0).toUpperCase();
            Iterable<Aircraft> fleet;

            switch (filterType) {
                case "MODEL":
                    fleet = listFleetController.getFleetByModel(
                            msg.params().size() > 1 ? msg.params().get(1) : "");
                    break;
                case "MAKER":
                    fleet = listFleetController.getFleetByMaker(
                            msg.params().size() > 1 ? msg.params().get(1) : "");
                    break;
                case "CAPACITY":
                    fleet = listFleetController.getFleetByCapacity(
                            msg.params().size() > 1 ? Integer.parseInt(msg.params().get(1)) : 0);
                    break;
                case "AGE":
                    fleet = listFleetController.getFleetByAge(
                            msg.params().size() > 1 ? Integer.parseInt(msg.params().get(1)) : 999);
                    break;
                default:
                    fleet = listFleetController.getMyCompanyFleet();
                    break;
            }

            int count = 0;
            for (Aircraft ignored : fleet) {
                count++;
            }

            return AtccProtocol.buildOkResponse("Fleet count: " + count);
        } catch (IllegalArgumentException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        }
    }

    // ── Route handlers ────────────────────────────────────────────────────────

    /**
     * Handles a create-route command.
     *
     * @param msg protocol message containing route designator, origin and
     * destination data
     * @return protocol response with success or error information
     */
    private String handleCreateRoute(final AtccProtocol.ProtocolMessage msg) {
        if (createFlightRouteController == null) {
            return AtccProtocol.buildErrorResponse("CreateFlightRouteController not available");
        }

        try {
            String payload = String.join(",", msg.params());
            String[] parts = AtccProtocol.parseCreateRoutePayload(payload);

            FlightRouteRegistrationDTO dto = new FlightRouteRegistrationDTO(parts[0], parts[1], parts[2]);
            createFlightRouteController.createRoute(dto);

            return AtccProtocol.buildOkResponse("Route " + parts[0] + " created.");
        } catch (IllegalArgumentException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        }
    }

    /**
     * Handles a deactivate-route command.
     *
     * @param msg protocol message containing route designator and deactivation
     * date
     * @return protocol response with success or error information
     */
    private String handleDeactivateRoute(final AtccProtocol.ProtocolMessage msg) {
        if (deactivateFlightRouteController == null) {
            return AtccProtocol.buildErrorResponse("DeactivateFlightRouteController not available");
        }

        try {
            String payload = String.join(",", msg.params());
            String[] parts = AtccProtocol.parseDeactivateRoutePayload(payload);

            FlightRoute route = deactivateFlightRouteController.deactivateRoute(
                    parts[0],
                    LocalDate.parse(parts[1]));

            return AtccProtocol.buildOkResponse(
                    "Route " + route.identity().value()
                            + " deactivated from " + route.deactivatedFrom());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        }
    }

    // ── Pilot Handlers ─────────────────────────────────────────────────────

    private String handleAddPilot(final AtccProtocol.ProtocolMessage msg) {
        System.out.println("[DEBUG] MENSAGEM RECEBIDA PELO SERVIDOR: " + msg.toString());
        if (addPilotController == null || aircraftModelRepo == null) {
            return AtccProtocol.buildErrorResponse("AddPilot dependencies not available.");
        }

        try {
            // Junta tudo para o caso do parser do protocolo ter cortado nos espaços
            String payload = String.join(",", msg.params());

            // Corta pelas vírgulas!
            String[] parts = payload.split(",");

            if (parts.length < 6) {
                // Se falhar, agora imprime o que recebeu para nós vermos!
                return AtccProtocol.buildErrorResponse("Invalid payload. Expected 6 parts, got " + parts.length + ". Received: " + payload);
            }

            String email = parts[0].trim();
            String name = parts[1].trim();
            String phone = parts[2].trim();
            String position = parts[3].trim();
            String password = parts[4].trim();
            String[] modelIdentifiers = parts[5].trim().split("\\|"); // IDs separados por pipe (|)

            // Vai à base de dados buscar os objetos AircraftModel
            java.util.List<eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel> selectedModels = new java.util.ArrayList<>();

            for (String identifier : modelIdentifiers) {
                String[] idParts = identifier.split(":");
                if (idParts.length != 2) {
                    return AtccProtocol.buildErrorResponse("Invalid aircraft model format. Expected ModelId:MakerName. Got: " + identifier);
                }

                var modelId = eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId.of(idParts[0].trim(), idParts[1].trim());

                aircraftModelRepo.findById(modelId).ifPresent(selectedModels::add);
            }

            if (selectedModels.isEmpty()) {
                return AtccProtocol.buildErrorResponse("No valid aircraft models found for the provided IDs.");
            }

            // Chama o TEU Controller original!
            addPilotController.addPilot(email, name, phone, position, selectedModels, password);

            return AtccProtocol.buildOkResponse("Pilot " + name + " added successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            return AtccProtocol.buildErrorResponse("Internal error: " + e.getMessage());
        }
    }

    private String handleListPilots(final AtccProtocol.ProtocolMessage msg) {
        if (listPilotsController == null) {
            return AtccProtocol.buildErrorResponse("ListPilotsController not available");
        }
        try {
            // Vai buscar a lista ao Controller
            Iterable<eapli.aisafe.pilotmanagement.domain.Pilot> roster = listPilotsController.listPilotsOfMyCompany();

            StringBuilder sb = new StringBuilder();
            int count = 0;

            for (eapli.aisafe.pilotmanagement.domain.Pilot p : roster) {
                sb.append(" [").append(p.email().toString()).append(" | ").append(p.getName()).append("]");
                count++;
            }

            if (count == 0) {
                return AtccProtocol.buildOkResponse("Pilot roster is empty.");
            }

            return AtccProtocol.buildOkResponse("Roster (" + count + "):" + sb.toString());

        } catch (IllegalArgumentException | IllegalStateException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing LIST_PILOTS", e);
            return AtccProtocol.buildErrorResponse("Internal error: " + e.getMessage());
        }
    }

    private String handleRemovePilot(final AtccProtocol.ProtocolMessage msg) {
        if (removePilotController == null) {
            return AtccProtocol.buildErrorResponse("RemovePilotController not available");
        }
        try {
            if (msg.params().isEmpty()) {
                return AtccProtocol.buildErrorResponse("Missing pilot email in payload.");
            }

            String targetEmail = msg.params().get(0).trim();
            eapli.aisafe.pilotmanagement.domain.Pilot pilotToDeactivate = null;

            // 1. Procurar o piloto na lista de pilotos ATIVOS da companhia
            Iterable<eapli.aisafe.pilotmanagement.domain.Pilot> activePilots = removePilotController.activePilotsOfMyCompany();
            for (eapli.aisafe.pilotmanagement.domain.Pilot p : activePilots) {
                if (p.email().toString().equalsIgnoreCase(targetEmail)) {
                    pilotToDeactivate = p;
                    break;
                }
            }

            // 2. Se não encontrou, é porque o email não existe ou o piloto já está desativado
            if (pilotToDeactivate == null) {
                return AtccProtocol.buildErrorResponse("Pilot '" + targetEmail + "' not found, already inactive, or belongs to another company.");
            }

            // 3. O momento da verdade: Desativar! (O Controller vai testar a "Regra de Ouro" dos voos atribuídos)
            removePilotController.deactivatePilot(pilotToDeactivate);

            return AtccProtocol.buildOkResponse("Pilot " + targetEmail + " successfully deactivated.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return AtccProtocol.buildErrorResponse(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing REMOVE_PILOT", e);
            return AtccProtocol.buildErrorResponse("Internal error: " + e.getMessage());
        }
    }

    private String handleListModels(final AtccProtocol.ProtocolMessage msg) {
        if (addPilotController == null) {
            return AtccProtocol.buildErrorResponse("AddPilotController not available.");
        }
        try {
            // Usa o método que tu já tinhas feito no teu Controller!
            Iterable<eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel> models = addPilotController.getAvailableAircraftModels();

            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (var m : models) {
                if (count > 0) sb.append(" | ");
                sb.append(m.identity().modelId()).append(":").append(m.identity().makerName());
                count++;
            }

            if (count == 0) return AtccProtocol.buildOkResponse("No models available.");

            return AtccProtocol.buildOkResponse(sb.toString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing LIST_MODELS", e);
            return AtccProtocol.buildErrorResponse("Internal error: " + e.getMessage());
        }
    }

    // ── Logging / cleanup ─────────────────────────────────────────────────────

    /**
     * Logs a remote access event for this client connection.
     *
     * <p>
     * Logging is skipped when no {@link RemoteLoggingService} is configured.
     * Failures while logging are recorded but do not interrupt command
     * processing.
     * </p>
     *
     * @param email user email associated with the event
     * @param eventType type of access event
     */
    private void logRemoteAccess(String email, EventType eventType) {
        if (remoteLoggingService != null) {
            try {
                String ip = clientSocket.getInetAddress().getHostAddress();
                int port = clientSocket.getPort();

                remoteLoggingService.logEvent(email, ip, port, ServiceType.US078, eventType);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Failed to log remote access", e);
            }
        }
    }

    /**
     * Closes the connection resources while ignoring individual close failures.
     */
    private void closeQuietly() {
        try {
            if (reader != null) reader.close();
        } catch (IOException ignored) {
        }

        try {
            if (writer != null) writer.close();
        } catch (IOException ignored) {
        }

        try {
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException ignored) {
        }
    }
}