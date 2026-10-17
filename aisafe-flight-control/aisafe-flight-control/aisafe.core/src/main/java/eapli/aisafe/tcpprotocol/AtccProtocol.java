package eapli.aisafe.tcpprotocol;

import eapli.aisafe.fleetmanagement.dto.AircraftRegistrationDTO;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that builds and parses ATCC TCP protocol messages.
 *
 * <p>
 * The ATCC protocol is a simple text-based, newline-terminated protocol used by
 * the Air Transport Company Collaborator client application and the AISafe TCP
 * server. Each request starts with a command name followed by optional command
 * parameters.
 * </p>
 *
 * <p>
 * Supported request examples:
 * </p>
 *
 * <pre>
 * AUTH &lt;email&gt; &lt;password&gt;
 * ADD_AIRCRAFT &lt;csv_payload&gt;
 * DECOMMISSION_AIRCRAFT &lt;registration&gt;
 * LIST_FLEET [ALL|MODEL|MAKER|CAPACITY|AGE] [value]
 * CREATE_ROUTE &lt;designator,origin,destination&gt;
 * DEACTIVATE_ROUTE &lt;designator,date&gt;
 * VALIDATE_FLIGHT_PLAN &lt;flightPlanId&gt;|&lt;dslContent&gt;
 * LOGOUT
 * </pre>
 *
 * <p>
 * Supported response examples:
 * </p>
 *
 * <pre>
 * AUTH_OK &lt;email&gt;
 * AUTH_FAIL
 * OK &lt;message&gt;
 * ERROR &lt;message&gt;
 * BYE
 * </pre>
 *
 * <p>
 * This class contains only static helper methods and should not be
 * instantiated.
 * </p>
 */
public class AtccProtocol {

    /**
     * Authentication success response prefix.
     */
    public static final String AUTH_OK = "AUTH_OK";

    /**
     * Authentication failure response.
     */
    public static final String AUTH_FAIL = "AUTH_FAIL";

    /**
     * Generic successful command response prefix.
     */
    public static final String OK = "OK";

    /**
     * Generic failed command response prefix.
     */
    public static final String ERROR = "ERROR";

    /**
     * Logout response sent when the session is closed.
     */
    public static final String BYE = "BYE";

    /**
     * Protocol version number.
     */
    public static final int VERSION = 1;

    /**
     * Parses one raw protocol line into a structured protocol message.
     *
     * <p>
     * The parser extracts the command name and splits the payload according to
     * the command type. Authentication and fleet listing use space-separated
     * parameters, while most business commands use comma-separated payloads.
     * The {@code VALIDATE_FLIGHT_PLAN} command uses a pipe-separated payload.
     * </p>
     *
     * @param rawLine raw protocol line received from the TCP connection
     * @return parsed protocol message
     */
    public static ProtocolMessage parse(final String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            return new ProtocolMessage("UNKNOWN", List.of());
        }

        String trimmed = rawLine.trim();
        int spaceIdx = trimmed.indexOf(' ');

        String command;
        String payload;

        if (spaceIdx > 0) {
            command = trimmed.substring(0, spaceIdx).toUpperCase();
            payload = trimmed.substring(spaceIdx + 1).trim();
        } else {
            command = trimmed.toUpperCase();
            payload = "";
        }

        List<String> params = new ArrayList<>();

        if (!payload.isBlank()) {
            if (command.equals("DECOMMISSION_AIRCRAFT") || command.equals("LOGOUT")) {
                params.add(payload);
            } else if (command.equals("AUTH") || command.equals("LIST_FLEET")) {
                String[] parts = payload.split(" ", 2);
                for (String part : parts) {
                    params.add(part.trim());
                }
            } else if (command.equals("VALIDATE_FLIGHT_PLAN")) {
                params.add(payload); // single param: "<id>|<dsl>"  // US085
            } else {
                String[] parts = payload.split(",", -1);
                for (String part : parts) {
                    params.add(part.trim());
                }
            }
        }

        return new ProtocolMessage(command, params);
    }

    /**
     * Builds an authentication command.
     *
     * @param email user email
     * @param password user password
     * @return protocol authentication command
     */
    public static String buildAuth(final String email, final String password) {
        return "AUTH " + email + " " + password;
    }

    /**
     * Builds an add-aircraft command from an aircraft registration DTO.
     *
     * @param dto aircraft registration data
     * @return protocol add-aircraft command
     */
    public static String buildAddAircraft(final AircraftRegistrationDTO dto) {
        return "ADD_AIRCRAFT " + dto.registration + "," + dto.modelId + "," +
                dto.makerName + "," + dto.country + "," +
                dto.firstClassSeats + "," + dto.businessSeats + "," +
                dto.economySeats + "," + dto.status + "," + dto.manufacturingYear;
    }

    /**
     * Parses an add-aircraft CSV payload into an aircraft registration DTO.
     *
     * @param payload comma-separated aircraft registration payload
     * @return aircraft registration DTO
     * @throws IllegalArgumentException if the payload does not contain the
     * required nine values
     * @throws NumberFormatException if a numeric field cannot be parsed
     */
    public static AircraftRegistrationDTO parseAddAircraftPayload(final String payload) {
        String[] parts = payload.split(",", -1);

        if (parts.length < 9) {
            throw new IllegalArgumentException(
                    "ADD_AIRCRAFT requires 9 comma-separated values. Got: " + parts.length);
        }

        return new AircraftRegistrationDTO(
                parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim(),
                Integer.parseInt(parts[4].trim()), Integer.parseInt(parts[5].trim()),
                Integer.parseInt(parts[6].trim()), parts[7].trim(),
                Integer.parseInt(parts[8].trim()));
    }

    /**
     * Builds a decommission-aircraft command.
     *
     * @param registration aircraft registration number
     * @return protocol decommission-aircraft command
     */
    public static String buildDecommissionAircraft(final String registration) {
        return "DECOMMISSION_AIRCRAFT " + registration;
    }

    /**
     * Builds a fleet listing command.
     *
     * <p>
     * If the filter type is {@code null} or {@code ALL}, the command requests
     * the complete fleet. Otherwise, the filter type and optional filter value
     * are included.
     * </p>
     *
     * @param filterType fleet filter type
     * @param filterValue optional fleet filter value
     * @return protocol list-fleet command
     */
    public static String buildListFleet(final String filterType, final String filterValue) {
        if (filterType == null || filterType.equals("ALL")) {
            return "LIST_FLEET ALL";
        }

        return "LIST_FLEET " + filterType + "," + (filterValue != null ? filterValue : "");
    }

    /**
     * Builds a create-route command.
     *
     * @param designator route designator
     * @param originIata origin airport IATA code
     * @param destIata destination airport IATA code
     * @return protocol create-route command
     */
    public static String buildCreateRoute(final String designator, final String originIata, final String destIata) {
        return "CREATE_ROUTE " + designator + "," + originIata + "," + destIata;
    }

    /**
     * Parses a create-route payload.
     *
     * @param payload comma-separated route payload
     * @return array containing route designator, origin IATA and destination
     * IATA
     * @throws IllegalArgumentException if the payload does not contain the
     * required three values
     */
    public static String[] parseCreateRoutePayload(final String payload) {
        String[] parts = payload.split(",", -1);

        if (parts.length < 3) {
            throw new IllegalArgumentException(
                    "CREATE_ROUTE requires 3 comma-separated values. Got: " + parts.length);
        }

        return new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim()};
    }

    /**
     * Builds a deactivate-route command.
     *
     * @param designator route designator
     * @param date deactivation date in {@code yyyy-MM-dd} format
     * @return protocol deactivate-route command
     */
    public static String buildDeactivateRoute(final String designator, final String date) {
        return "DEACTIVATE_ROUTE " + designator + "," + date;
    }

    /**
     * Parses and validates a deactivate-route payload.
     *
     * @param payload comma-separated payload containing route designator and
     * deactivation date
     * @return array containing route designator and deactivation date
     * @throws IllegalArgumentException if the payload does not contain the
     * required two values
     * @throws IllegalArgumentException if the date is not in {@code yyyy-MM-dd}
     * format
     */
    public static String[] parseDeactivateRoutePayload(final String payload) {
        String[] parts = payload.split(",", -1);

        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "DEACTIVATE_ROUTE requires 2 comma-separated values. Got: " + parts.length);
        }

        try {
            java.time.LocalDate.parse(parts[1].trim());
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }

        return new String[]{parts[0].trim(), parts[1].trim()};
    }

    // ── US085 — Validate Flight Plan ──────────────────────────────────────────

    /**
     * Builds a validate-flight-plan command (US085).
     *
     * @param flightPlanId flight plan identifier (may be blank for dry-run)
     * @param dslContent DSL content to validate
     * @return protocol validate-flight-plan command
     */
    public static String buildValidateFlightPlan(final String flightPlanId,
                                                 final String dslContent) {
        String id = (flightPlanId != null) ? flightPlanId.trim() : "";
        return "VALIDATE_FLIGHT_PLAN " + id + "|" + dslContent;
    }

    /**
     * Parses a validate-flight-plan payload.
     *
     * @param payload the part after {@code VALIDATE_FLIGHT_PLAN } (already trimmed)
     * @return {@code String[2]} — {@code [0]} flight plan id (may be blank),
     * {@code [1]} DSL content
     * @throws IllegalArgumentException if the payload does not contain a pipe
     * separator or the DSL content is blank
     */
    public static String[] parseValidateFlightPlanPayload(final String payload) {
        int pipeIdx = payload.indexOf('|');
        if (pipeIdx < 0) {
            throw new IllegalArgumentException(
                    "VALIDATE_FLIGHT_PLAN payload must be: <flightPlanId>|<dslContent>");
        }
        String id  = payload.substring(0, pipeIdx).trim();
        String dsl = payload.substring(pipeIdx + 1);
        if (dsl.isBlank()) {
            throw new IllegalArgumentException("DSL content must not be empty.");
        }
        return new String[]{id, dsl};
    }

    // ── Pilot commands ────────────────────────────────────────────────────────

    /**
     * Builds an add-pilot command.
     *
     * @param name pilot name
     * @param email pilot email
     * @param phone pilot phone number
     * @return protocol add-pilot command
     */
    public static String buildAddPilot(String email, String name, String phone, String position, String password, String modelIds) {
        return "ADD_PILOT " + email + "," + name + "," + phone + "," + position + "," + password + "," + modelIds;
    }

    /**
     * Builds a list-pilots command.
     *
     * @return protocol list-pilots command
     */
    public static String buildListPilots() {
        return "LIST_PILOTS";
    }

    /**
     * Builds a remove-pilot command.
     *
     * @param email pilot email
     * @return protocol remove-pilot command
     */
    public static String buildRemovePilot(final String email) {
        return "REMOVE_PILOT " + email;
    }

    public static String buildListModels() {
        return "LIST_MODELS";
    }

    /**
     * Builds a logout command.
     *
     * @return protocol logout command
     */
    public static String buildLogout() {
        return "LOGOUT";
    }

    /**
     * Builds a successful protocol response.
     *
     * @param message response message
     * @return response formatted as {@code OK <message>}
     */
    public static String buildOkResponse(final String message) {
        return OK + " " + message;
    }

    /**
     * Builds an error protocol response.
     *
     * @param message error message
     * @return response formatted as {@code ERROR <message>}
     */
    public static String buildErrorResponse(final String message) {
        return ERROR + " " + message;
    }

    /**
     * Immutable representation of a parsed ATCC protocol message.
     *
     * <p>
     * The message contains the normalized command name and the parsed command
     * parameters. Convenience methods are provided to identify the command type.
     * </p>
     */
    public static class ProtocolMessage {

        /**
         * Normalized command name.
         */
        private final String command;

        /**
         * Parsed command parameters.
         */
        private final List<String> params;

        /**
         * Creates a parsed protocol message.
         *
         * @param command normalized command name
         * @param params parsed command parameters
         */
        public ProtocolMessage(final String command, final List<String> params) {
            this.command = command;
            this.params = params;
        }

        /**
         * Returns the command name.
         *
         * @return normalized command name
         */
        public String command() { return command; }

        /**
         * Returns the parsed command parameters.
         *
         * @return command parameters
         */
        public List<String> params() { return params; }

        /**
         * Checks whether this message is an authentication command.
         *
         * @return {@code true} if the command is {@code AUTH}
         */
        public boolean isAuth() { return "AUTH".equals(command); }

        /**
         * Checks whether this message is an add-aircraft command.
         *
         * @return {@code true} if the command is {@code ADD_AIRCRAFT}
         */
        public boolean isAddAircraft() { return "ADD_AIRCRAFT".equals(command); }

        /**
         * Checks whether this message is a decommission-aircraft command.
         *
         * @return {@code true} if the command is {@code DECOMMISSION_AIRCRAFT}
         */
        public boolean isDecommissionAircraft() { return "DECOMMISSION_AIRCRAFT".equals(command); }

        /**
         * Checks whether this message is a list-fleet command.
         *
         * @return {@code true} if the command is {@code LIST_FLEET}
         */
        public boolean isListFleet() { return "LIST_FLEET".equals(command); }

        /**
         * Checks whether this message is a create-route command.
         *
         * @return {@code true} if the command is {@code CREATE_ROUTE}
         */
        public boolean isCreateRoute() { return "CREATE_ROUTE".equals(command); }

        /**
         * Checks whether this message is a deactivate-route command.
         *
         * @return {@code true} if the command is {@code DEACTIVATE_ROUTE}
         */
        public boolean isDeactivateRoute() { return "DEACTIVATE_ROUTE".equals(command); }

        /**
         * Checks whether this message is a validate-flight-plan command (US085).
         *
         * @return {@code true} if the command is {@code VALIDATE_FLIGHT_PLAN}
         */
        public boolean isValidateFlightPlan() { return "VALIDATE_FLIGHT_PLAN".equals(command); }  // US085

        /**
         * Checks whether this message is a logout command.
         *
         * @return {@code true} if the command is {@code LOGOUT}
         */
        public boolean isLogout() { return "LOGOUT".equals(command); }

        public boolean isAddPilot() { return "ADD_PILOT".equals(command); }
        public boolean isListPilots() { return "LIST_PILOTS".equals(command); }
        public boolean isRemovePilot() { return "REMOVE_PILOT".equals(command); }
        public boolean isListModels() { return "LIST_MODELS".equals(command); }
    }
}